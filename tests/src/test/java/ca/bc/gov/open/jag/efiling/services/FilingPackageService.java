package ca.bc.gov.open.jag.efiling.services;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;

import java.text.MessageFormat;

public class FilingPackageService {

    @Value("${EFILING_HOST:http://localhost:8080}")
    private String eFilingHost;

    public Response getByPackageId(String accessToken, int packageId) {

        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RequestSpecification request = RestAssured
                .given()
                .auth()
                .preemptive()
                .oauth2(accessToken);

        return request.when()
                .get(MessageFormat.format("{0}/filingpackages/{1}", eFilingHost, packageId))
                .then()
                .extract()
                .response();
    }
}
