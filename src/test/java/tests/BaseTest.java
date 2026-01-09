package tests;

import Config.ConfigReader;
import Config.JsonReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected JsonReader testData;
    protected static String userToken;
    protected static String adminToken;
    protected static Integer userId;
    protected static Integer productId;
    protected static String productName;
    protected static Integer createdProductId;
    protected static Integer buggyProductId;
    protected static Integer categoryId;
    protected static String categoryName;
    protected static Integer brandId;
    protected static Integer invoiceId;
    protected static String invoiceNumber;
    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {

        RestAssured.baseURI = ConfigReader.getBaseUrl();
        requestSpec = new RequestSpecBuilder()
                .setContentType("application/json")
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();

        System.out.println("===========================================");
        System.out.println("Base Test Setup Complete");
        System.out.println("Base URL: " + RestAssured.baseURI);
        System.out.println("===========================================");
    }

    protected RequestSpecification getAuthRequest(String token) {
        return RestAssured.given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token);
    }

    protected RequestSpecification getRequest() {
        return RestAssured.given()
                .spec(requestSpec);
    }
}