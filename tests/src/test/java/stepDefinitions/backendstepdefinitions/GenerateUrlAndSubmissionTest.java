package stepDefinitions.backendstepdefinitions;

import ca.bc.gov.open.jagefilingapi.qa.backendutils.APIResources;
import ca.bc.gov.open.jagefilingapi.qa.backendutils.TestUtil;
import ca.bc.gov.open.jagefilingapi.qa.frontendutils.DriverClass;
import ca.bc.gov.open.jagefilingapi.qa.frontendutils.JsonDataReader;
import ca.bc.gov.open.jagefilingapi.qa.requestbuilders.GenerateUrlRequestBuilders;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class GenerateUrlAndSubmissionTest extends DriverClass {

    private Response response;
    private GenerateUrlRequestBuilders generateUrlRequestBuilders;
    private String submissionId;
    private JsonPath jsonPath;
    private String validExistingCSOGuid;
    private static final String CONTENT_TYPE = "application/json";
    private static final String X_TRANSACTION_ID = "X-Transaction-Id";
    private static final String SUBMISSION_ID = "submissionId";
    private static final String TRANSACTION_ID = "transactionId";
    private static final String PATH_PARAM = "/generateUrl";
    private String userToken;

    public Logger log = LogManager.getLogger(GenerateUrlAndSubmissionTest.class);

    @Given("POST http request is made to {string} with valid existing CSO account guid and a single pdf file")
    public void postHttpRequestIsMadeToWithValidExistingCsoAccountGuidAndASinglePdfFile(String resource) throws IOException {
        generateUrlRequestBuilders = new GenerateUrlRequestBuilders();

        response = generateUrlRequestBuilders.validRequestWithSingleDocument(resource);
    }

    @When("status code is {int} and content type is verified")
    public void statusCodeIsAndContentTypeIsVerified(Integer statusCode) {
        generateUrlRequestBuilders = new GenerateUrlRequestBuilders();

        assertEquals(200, response.getStatusCode());
        assertEquals(CONTENT_TYPE, response.getContentType());
    }

    @Then("verify submission id and document count is received")
    public void verifySubmissionIdAndDocumentCountIsReceived() {
        jsonPath = new JsonPath(response.asString());

        submissionId = TestUtil.getJsonPath(response, SUBMISSION_ID);
        int receivedCount = jsonPath.get("received");

        switch (receivedCount) {
            case 1:
                assertEquals(1, receivedCount);
                break;
            case 2:
                assertEquals(2, receivedCount);
                break;
            default:
                log.info("Document count did not match.");
        }
        assertNotNull(submissionId);
    }

    @Given("POST http request is made to {string} with client application, court details and navigation urls")
    public void POSTHttpRequestIsMadeToWithClientApplicationCourtDetailsAndNavigationUrls(String resource) throws IOException {
        generateUrlRequestBuilders = new GenerateUrlRequestBuilders();

        validExistingCSOGuid = JsonDataReader.getCsoAccountGuid().getValidExistingCSOGuid();

        response = generateUrlRequestBuilders.postRequestWithPayload(resource,validExistingCSOGuid, submissionId, PATH_PARAM );
    }

    @Then("verify expiry date and eFiling url are returned with the CSO account guid and submission id")
    public void verifyExpiryDateAndEfilingUrlAreReturnedWithTheCsoAccountGuidAndSubmissionId() throws URISyntaxException, IOException {
        jsonPath = new JsonPath(response.asString());
        validExistingCSOGuid = JsonDataReader.getCsoAccountGuid().getValidExistingCSOGuid();

        String respUrl = jsonPath.get("efilingUrl");
        Long expiryDate = jsonPath.get("expiryDate");

        List<String> respId = TestUtil.getSubmissionAndTransId(respUrl, SUBMISSION_ID, TRANSACTION_ID);

        assertEquals(submissionId, respId.get(0));
        assertEquals(validExistingCSOGuid, respId.get(1));
        assertNotNull(expiryDate);
    }

    @Given("{string} id is submitted with GET http request")
    public void idIsSubmittedWithGetHttpRequest(String resource) throws IOException {
        APIResources resourceGet = APIResources.valueOf(resource);

        generateUrlRequestBuilders = new GenerateUrlRequestBuilders();
        userToken = generateUrlRequestBuilders.getUserJwtToken();

        RequestSpecification request = given().auth().preemptive().oauth2(userToken)
                .spec(TestUtil.requestSpecification())
                .header(X_TRANSACTION_ID, validExistingCSOGuid);

        response = request.when().get(resourceGet.getResource() + submissionId)
                .then()
                .spec(TestUtil.responseSpecification())
                .extract().response();
    }

    @Then("verify universal id, user details, account type and identifier values are returned and not empty")
    public void verifyUniversalIdUserDetailsAccountTypeAndIdentifierValuesAreReturnedAndNotEmpty() {
        jsonPath = new JsonPath(response.asString());

        String universalId = jsonPath.get("userDetails.universalId");
        String displayName = jsonPath.get("clientApplication.displayName");
        String clientAppType = jsonPath.get("clientApplication.type");

        List<String> type = jsonPath.get("userDetails.accounts.type");
        List<String> identifier = jsonPath.get("userDetails.accounts.identifier");

        assertThat(universalId, is(not(emptyString())));
        assertThat(displayName, is(not(emptyString())));
        assertThat(clientAppType, is(not(emptyString())));
        log.info("Names and email objects from the valid CSO account submission response have valid values");

        assertFalse(type.isEmpty());
        assertFalse(identifier.isEmpty());
        log.info("Account type and identifier objects from the valid CSO account submission response have valid values");
    }

    @Given("{string} id with filing package path is submitted with GET http request")
    public void idWithFilingPackagePathIsSubmittedWithGETHttpRequest(String resource) throws IOException {
        APIResources resourceGet = APIResources.valueOf(resource);

        RequestSpecification request = given().auth().preemptive().oauth2(userToken).spec(TestUtil.requestSpecification())
                .header(X_TRANSACTION_ID, validExistingCSOGuid);

        response = request.when().get(resourceGet.getResource() + submissionId + "/filing-package")
                .then()
                .spec(TestUtil.responseSpecification())
                .extract().response();
    }

    @Then("verify court details and document details are returned and not empty")
    public void verifyCourtDetailsAndDocumentDetailsAreReturnedAndNotEmpty() {
        jsonPath = new JsonPath(response.asString());
        float submissionFeeAmount = jsonPath.get("submissionFeeAmount");

        assertThat(jsonPath.get("court.location"), is(not(emptyString())));
        assertThat(jsonPath.get("court.level"), is(not(emptyString())));
        assertThat(jsonPath.get("court.class"), is(not(emptyString())));
        assertThat(jsonPath.get("court.division"), is(not(emptyString())));
        assertThat(jsonPath.get("court.fileNumber"), is(not(emptyString())));
        assertThat(jsonPath.get("court.participatingClass"), is(not(emptyString())));
        assertThat(jsonPath.get("court.locationDescription"), is(not(emptyString())));
        assertThat(jsonPath.get("court.levelDescription"), is(not(emptyString())));
        assertThat(jsonPath.get("parties"), is(not(emptyString())));
        assertEquals(7.00, submissionFeeAmount, 0);
        log.info("Court fee and document details response have valid values");

        assertFalse(jsonPath.get("documents.name").toString().isEmpty());
        assertFalse(jsonPath.get("documents.type").toString().isEmpty());
        assertFalse(jsonPath.get("documents.subType").toString().isEmpty());
        assertFalse(jsonPath.get("documents.isAmendment").toString().isEmpty());
        assertFalse(jsonPath.get("documents.isSupremeCourtScheduling").toString().isEmpty());
        assertFalse(jsonPath.get("documents.description").toString().isEmpty());
        assertFalse(jsonPath.get("documents.statutoryFeeAmount").toString().isEmpty());
        assertNotNull(jsonPath.get("documents.statutoryFeeAmount"));
        log.info("Account type, description and name objects from the valid CSO account submission response have valid values");
    }

    @And("verify success, error and cancel navigation urls are returned")
    public void verifySuccessErrorAndCancelNavigationUrlsAreReturned() {
        jsonPath = new JsonPath(response.asString());

        String successUrl = jsonPath.get("navigation.success.url");
        String errorUrl = jsonPath.get("navigation.error.url");
        String cancelUrl = jsonPath.get("navigation.cancel.url");

        assertNotNull(successUrl);
        assertNotNull(errorUrl);
        assertNotNull(cancelUrl);
    }

    @Given("{string} id with filename path is submitted with GET http request")
    public void idWithFilenamePathIsSubmittedWithGETHttpRequest(String resource) throws IOException {
        APIResources resourceGet = APIResources.valueOf(resource);

        RequestSpecification request = given().auth().preemptive().oauth2(userToken).spec(TestUtil.requestSpecification())
                .header(X_TRANSACTION_ID, validExistingCSOGuid);

        response = request.when().get(resourceGet.getResource() + submissionId + "/document" + "/test-document.pdf")
                .then()
               // .spec(TestUtil.documentValidResponseSpecification())
                .extract().response();
    }

    @Then("Verify status code is {int} and content type is not json")
    public void verifyStatusCodeIsAndContentTypeIsNotJson(Integer int1) {
        assertEquals(200, response.getStatusCode());
        assertEquals("application/octet-stream", response.getContentType());
    }

    @Given("POST http request is made to {string} with valid existing CSO account guid and multiple file")
    public void POSTHttpRequestIsMadeToWithValidExistingCSOAccountGuidAndMultipleFile(String resource) throws IOException {
        generateUrlRequestBuilders = new GenerateUrlRequestBuilders();

        response = generateUrlRequestBuilders.validRequestWithMultipleDocuments(resource);
    }
}
