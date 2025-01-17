package ca.bc.gov.open.efilingdiligenclient.diligen.diligenServiceImpl;

import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenAuthService;
import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenProperties;
import ca.bc.gov.open.efilingdiligenclient.diligen.DiligenServiceImpl;
import ca.bc.gov.open.efilingdiligenclient.diligen.mapper.DiligenDocumentDetailsMapperImpl;
import ca.bc.gov.open.efilingdiligenclient.diligen.model.DiligenDocumentDetails;
import ca.bc.gov.open.efilingdiligenclient.exception.DiligenDocumentException;
import ca.bc.gov.open.jag.efilingdiligenclient.api.DocumentsApi;
import ca.bc.gov.open.jag.efilingdiligenclient.api.handler.ApiClient;
import ca.bc.gov.open.jag.efilingdiligenclient.api.handler.ApiException;
import ca.bc.gov.open.jag.efilingdiligenclient.api.model.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("DiligenServiceImpl test suite")
public class GetDocumentDetailsTest {
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String JWT = "IMMAJWT";
    private static final Object JSON_OBJECT = "{ \"garbage\":\"garbage\" }";
    public static final String FILE_NAME = "FILE_NAME";
    public static final String EXECUTION_STATUS = "EXECUTION_STATUS";
    public static final String STATUS = "PROCESSED";
    public static final String NOT_PROCESSED = "NOT_PROCESSED";
    public static final String NAME = "NAME";
    public static final String TYPE = "TYPE";
    public static final String STRING = "STRING";

    DiligenServiceImpl sut;

    @Mock
    DiligenAuthService diligenAuthServiceMock;

    @Mock
    DocumentsApi documentsApiMock;

    @BeforeAll
    public void beforeAll() throws ApiException {

        MockitoAnnotations.openMocks(this);

        DiligenProperties diligenProperties = new DiligenProperties();
        diligenProperties.setBasePath("http:/test");
        diligenProperties.setProjectIdentifier(1);
        diligenProperties.setUsername(USERNAME);
        diligenProperties.setPassword(PASSWORD);

        Mockito.when(diligenAuthServiceMock.getDiligenJWT(any(), any())).thenReturn(JWT);

        Mockito.when(documentsApiMock.getApiClient()).thenReturn(new ApiClient());

        Mockito.when(documentsApiMock.apiDocumentsFileIdDetailsGet(ArgumentMatchers.eq(BigDecimal.ONE.intValue()))).thenReturn(getMockData(STATUS));

        Mockito.when(documentsApiMock.apiDocumentsFileIdProjectFieldsGet(ArgumentMatchers.eq(BigDecimal.ONE.intValue()))).thenReturn(getMockAnswers());

        Mockito.when(documentsApiMock.apiDocumentsFileIdDetailsGet(ArgumentMatchers.eq(BigDecimal.TEN.intValue()))).thenReturn(getMockData(STATUS));

        Mockito.when(documentsApiMock.apiDocumentsFileIdProjectFieldsGet(ArgumentMatchers.eq(BigDecimal.TEN.intValue()))).thenThrow(new ApiException());

        Mockito.when(documentsApiMock.apiDocumentsFileIdDetailsGet(ArgumentMatchers.eq(BigDecimal.ZERO.intValue()))).thenThrow(new ApiException());

        sut = new DiligenServiceImpl(null, diligenProperties, diligenAuthServiceMock, null, documentsApiMock, new DiligenDocumentDetailsMapperImpl());

    }

    @Test
    @DisplayName("Ok: document was returned")
    public void withValidDocumentIdDocumentSubmissionReturned() {

        DiligenDocumentDetails result = sut.getDocumentDetails(BigDecimal.ONE);

       assertEquals(JSON_OBJECT, result.getMlJson());
       assertEquals(EXECUTION_STATUS, result.getExecutionStatus());
       assertEquals(FILE_NAME, result.getFileName());
       assertEquals(STATUS, result.getFileStatus());
       assertTrue(result.getOcr());
       assertTrue(result.getConverted());
       assertEquals(BigDecimal.ONE, result.getOutOfScope());
       assertEquals(JSON_OBJECT, result.getExtractedDocument());

       assertEquals(1, result.getAnswers().size());
       assertEquals(NAME, result.getAnswers().get(0).getName());
       assertEquals(1, result.getAnswers().get(0).getCreatedBy());
       assertEquals(1, result.getAnswers().get(0).getId());
       assertEquals(TYPE, result.getAnswers().get(0).getFieldType().getType());
       assertTrue(result.getAnswers().get(0).getFieldType().isMulti());
       assertEquals(1, result.getAnswers().get(0).getFieldType().getOptions().size());
       assertEquals(STRING, result.getAnswers().get(0).getFieldType().getOptions().get(0));
       assertEquals(1, result.getAnswers().get(0).getValues().size());
       assertEquals(STRING, result.getAnswers().get(0).getValues().get(0));

    }

    @Test
    @DisplayName("Error: API Exception thrown retrieving details")
    public void withApiErrorInDetailApiException() {

        Assertions.assertThrows(DiligenDocumentException.class, () -> sut.getDocumentDetails(BigDecimal.ZERO));

    }

    @Test
    @DisplayName("Error: API Exception thrown retrieving answers")
    public void withApiErrorInAnswersApiException() {

        Assertions.assertThrows(DiligenDocumentException.class, () -> sut.getDocumentDetails(BigDecimal.TEN));

    }

    private InlineResponse2003 getMockData(String status) {

        InlineResponse2003 inlineResponse2003 = new InlineResponse2003();
        InlineResponse2003Data data = new InlineResponse2003Data();
        InlineResponse2003DataFileDetails fileDetails = new InlineResponse2003DataFileDetails();
        fileDetails.setExtractedDocument(JSON_OBJECT);
        fileDetails.setMlJson(JSON_OBJECT);
        fileDetails.setFileStatus(status);
        fileDetails.setFileName(FILE_NAME);
        fileDetails.setExecutionStatus(EXECUTION_STATUS);
        fileDetails.setIsOcr(true);
        fileDetails.setIsConverted(true);
        fileDetails.setOutOfScope(BigDecimal.ONE);
        data.setFileDetails(fileDetails);
        inlineResponse2003.setData(data);

        return inlineResponse2003;

    }

    private ProjectFieldsResponse getMockAnswers() {
        ProjectFieldsResponse projectFieldsResponse = new ProjectFieldsResponse();
        ProjectFieldsResponseData projectFieldsResponseData = new ProjectFieldsResponseData();

        List<Field> fields = new ArrayList<>();
        Field field = new Field();
        field.setId(1);
        field.setCreatedBy(1);
        field.setName(NAME);

        FieldType fieldType = new FieldType();
        fieldType.setMulti(true);
        fieldType.setType(TYPE);
        List<String> strings = new ArrayList<>();
        strings.add(STRING);
        fieldType.setOptions(strings);
        field.setFieldType(fieldType);
        field.setValues(strings);

        fields.add(field);

        projectFieldsResponseData.setFields(fields);
        projectFieldsResponse.setData(projectFieldsResponseData);

        return projectFieldsResponse;
    }

}
