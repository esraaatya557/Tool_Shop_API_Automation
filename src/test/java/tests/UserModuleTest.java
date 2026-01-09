package tests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import Config.JsonReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("TOOL SHOP")
@Feature("User Management")
@Severity(SeverityLevel.CRITICAL)
@Owner("Esraa Atya")

@Test(groups = "user")
public class UserModuleTest extends BaseTest {


    @Description("Register New User ")
    @Test(priority = 1)
    public void testRegisterUser() {
        System.out.println("\n=== Test: Register New User ===");
        String reqbody = "{\n" +
                "  \"first_name\": \"" + testData.getJsonData("first_name") + "\",\n" +
                "  \"last_name\": \"" + testData.getJsonData("last_name") + "\",\n" +
                "  \"address\": \"" + testData.getJsonData("address") + "\",\n" +
                "  \"city\": \"" + testData.getJsonData("city") + "\",\n" +
                "  \"state\": \"" + testData.getJsonData("state") + "\",\n" +
                "  \"country\": \"" + testData.getJsonData("country") + "\",\n" +
                "  \"postcode\": \"" + testData.getJsonData("postcode") + "\",\n" +
                "  \"phone\": \"" + testData.getJsonData("phone") + "\",\n" +
                "  \"dob\": \"" + testData.getJsonData("dob") + "\",\n" +
                "  \"email\": \"" + testData.getJsonData("email") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("password") + "\"\n" +
                "}";
        Response response = getRequest().body(reqbody)
                .when().post("/users/register")
                .then().statusCode(201)
                .body("email", equalTo(testData.getJsonData("email")))
                .body("id", notNullValue())
                .extract().response();
        userId = response.jsonPath().get("id");
        System.out.println("User registered successfully with ID: " + userId);
        response.prettyPrint();
    }

    @Description("Login User ")
    @Test(priority = 2, dependsOnMethods = "testRegisterUser")
    public void testLoginUser() {
        System.out.println("\n=== Test: Login User ===");

        String requestBody = "{\n" +
                "  \"email\": \"" + testData.getJsonData("email") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("password") + "\"\n" +
                "}";
        Response response = getRequest().body(requestBody)
                .when().post("/users/login")
                .then().statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("bearer"))
                .extract().response();

        userToken = response.jsonPath().getString("access_token");
        System.out.println("Token received (length: " + userToken.length() + ")");
        System.out.println(userToken);
    }

    @Description("Get Current User Info")
    @Test(priority = 3, dependsOnMethods = "testLoginUser")
    public void testGetCurrentUserInfo() {
        System.out.println("\n=== Test: Get Current User Info ===");

        getAuthRequest(userToken)
                .when().get("/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(testData.getJsonData("email")))
                .body("first_name", equalTo(testData.getJsonData("first_name")))
                .body("last_name", equalTo(testData.getJsonData("last_name")));

        System.out.println("User profile retrieved successfully");
        System.out.println("User ID: " + userId);
    }

    @Description("Update Current User")
    @Test(priority = 4, dependsOnMethods = "testLoginUser")
    public void testUpdateCurrentUser() {
        System.out.println("\n=== Test: Update Current User ===");

        String requestBody = "{\n" +
                "  \"first_name\": \"" + testData.getJsonData("first_name") + "\",\n" +
                "  \"last_name\": \"" + testData.getJsonData("updatedLastName") + "\",\n" +
                "  \"address\": \"" + testData.getJsonData("address") + "\",\n" +
                "  \"city\": \"" + testData.getJsonData("updatedCity") + "\",\n" +
                "  \"state\": \"" + testData.getJsonData("state") + "\",\n" +
                "  \"country\": \"" + testData.getJsonData("country") + "\",\n" +
                "  \"postcode\": \"" + testData.getJsonData("postcode") + "\",\n" +
                "  \"phone\": \"" + testData.getJsonData("phone") + "\",\n" +
                "  \"dob\": \"" + testData.getJsonData("dob") + "\",\n" +
                "  \"email\": \"" + testData.getJsonData("email") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("password") + "\"\n" +
                "}";

        getAuthRequest(userToken)
                .body(requestBody)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        System.out.println("User profile updated successfully");
    }

    @Description(" Logout User")
    @Test(priority = 5, dependsOnMethods = "testUpdateCurrentUser")
    public void testLogoutUser() {
        System.out.println("\n=== Test: Logout User ===");

        getAuthRequest(userToken)
                .when()
                .get("/users/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("Successfully logged out"));

        System.out.println("User logged out successfully");
    }

    @Description(" Register with Invalid Email")
    @Test(priority = 6)
    public void testRegisterWithInvalidEmail() {
        System.out.println("\n=== Negative Test: Register with Invalid Email ===");

        String requestBody = "{\n" +
                "  \"first_name\": \"" + testData.getJsonData("first_name") + "\",\n" +
                "  \"last_name\": \"" + testData.getJsonData("updatedLastName") + "\",\n" +
                "  \"address\": \"" + testData.getJsonData("address") + "\",\n" +
                "  \"city\": \"" + testData.getJsonData("updatedCity") + "\",\n" +
                "  \"state\": \"" + testData.getJsonData("state") + "\",\n" +
                "  \"country\": \"" + testData.getJsonData("country") + "\",\n" +
                "  \"postcode\": \"" + testData.getJsonData("postcode") + "\",\n" +
                "  \"phone\": \"" + testData.getJsonData("phone") + "\",\n" +
                "  \"dob\": \"" + testData.getJsonData("dob") + "\",\n" +
                "  \"email\": \"" + testData.getJsonData("invalidEmail") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("password") + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/users/register");

        int statusCode = response.getStatusCode();
        System.out.println("Response Status: " + statusCode);

        if (statusCode == 403 || statusCode == 400) {
            System.out.println("Correctly rejected invalid email");
        } else if (statusCode == 201) {
            System.out.println("BUG: API accepted invalid email format!");
        }
    }

    @Description("Register with Duplicate Email")
    @Test(priority = 7, dependsOnMethods = "testRegisterUser")
    public void testRegisterWithDuplicateEmail() {
        System.out.println("\n=== Negative Test: Register with Duplicate Email ===");

        String requestBody = "{\n" +
                "  \"first_name\": \"" + testData.getJsonData("first_name") + "\",\n" +
                "  \"last_name\": \"" + testData.getJsonData("updatedLastName") + "\",\n" +
                "  \"address\": \"" + testData.getJsonData("address") + "\",\n" +
                "  \"city\": \"" + testData.getJsonData("updatedCity") + "\",\n" +
                "  \"state\": \"" + testData.getJsonData("state") + "\",\n" +
                "  \"country\": \"" + testData.getJsonData("country") + "\",\n" +
                "  \"postcode\": \"" + testData.getJsonData("postcode") + "\",\n" +
                "  \"phone\": \"" + testData.getJsonData("phone") + "\",\n" +
                "  \"dob\": \"" + testData.getJsonData("dob") + "\",\n" +
                "  \"email\": \"" + testData.getJsonData("invalidEmail") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("password") + "\"\n" +
                "}";
        ;

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/users/register");

        int statusCode = response.getStatusCode();

        if (statusCode == 422) {
            System.out.println("Correctly rejected duplicate email");
        } else if (statusCode == 201) {
            System.out.println("BUG: API accepted duplicate email!");
        }
    }

    @Description("Login with Wrong Password ")
    @Test(priority = 8)
    public void testLoginWithWrongPassword() {
        System.out.println("\n=== Negative Test: Login with Wrong Password ===");

        String requestBody = "{\n" +
                "  \"email\": \"" + testData.getJsonData("email") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("wrongPass") + "\"\n" +
                "}";

        getRequest()
                .body(requestBody)
                .when()
                .post("/users/login")
                .then()
                .statusCode(401);

        System.out.println("Correctly rejected wrong password");
    }

    @Description("Login with Non-existent Email")
    @Test(priority = 9)
    public void testLoginWithNonExistentEmail() {
        System.out.println("\n=== Negative Test: Login with Non-existent Email ===");

        String requestBody = "{\n" +
                "  \"email\": \"" + testData.getJsonData("wrongEmail") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("wrongPass") + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/users/login");

        int statusCode = response.getStatusCode();
        if (statusCode == 401) {
            System.out.println("Correctly rejected non-existent user");
        }
    }

    @Description("Get Profile Without Token")
    @Test(priority = 10)
    public void testGetProfileWithoutToken() {
        System.out.println("\n=== Negative Test: Get Profile Without Token ===");

        getRequest()
                .when()
                .get("/users/me")
                .then()
                .statusCode(401)
                .body("message", notNullValue());

        System.out.println("Correctly rejected request without token");
    }

    @BeforeClass
    protected void preCondition() {
        testData = new JsonReader("user-data");
    }
}