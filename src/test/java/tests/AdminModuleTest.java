package tests;

import Config.ConfigReader;
import Config.JsonReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("TOOL SHOP")
@Feature("Admin Management")
@Severity(SeverityLevel.CRITICAL)
@Owner("Esraa Atya")
@Test(groups = "admin")
public class AdminModuleTest extends BaseTest {

    @Description("Login Admin")
    @Test(priority = 1)
    public void testLoginAdmin() {
        System.out.println("\n=== Test: Login Admin ===");

        String requestBody = "{\n" +
                "  \"email\": \"" + ConfigReader.getAdminEmail() + "\",\n" +
                "  \"password\": \"" + ConfigReader.getAdminPassword() + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("access_token", notNullValue())
                .body("token_type", equalTo("bearer"))
                .extract().response();

        adminToken = response.jsonPath().getString("access_token");
        System.out.println("Admin logged in successfully");
        System.out.println("Admin token received");
    }

    @Description("Get Specific User (Admin)")
    @Test(priority = 2, dependsOnMethods = "testLoginAdmin")
    public void testGetSpecificUser() {
        System.out.println("\n=== Test: Get Specific User (Admin) ===");

        getAuthRequest(adminToken)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(200)
                .body("id", equalTo(userId))
                .body("email", equalTo(testData.getJsonData("email")));

        System.out.println("User details retrieved by Admin");
    }

    @Description("Delete Specific User (Admin)")
    @Test(priority = 3, dependsOnMethods = {"testGetSpecificUser"})
    public void testDeleteUser() {
        System.out.println("\n=== Test: Delete User (Admin) ===");

        getAuthRequest(adminToken)
                .when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);

        System.out.println("User deleted successfully");
        System.out.println("Deleted user ID: " + userId);
    }

    @Test(priority = 4, description = "Admin: Verify User Deleted")
    public void testVerifyUserDeleted() {
        System.out.println("\n=== Test: Verify User Deleted ===");

        getAuthRequest(adminToken)
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(404);
        System.out.println("Confirmed: User no longer exists");
    }

    @Description("Logout Admin")
    @Test(priority = 5, dependsOnMethods = "testLoginAdmin")
    public void testLogoutAdmin() {
        System.out.println("\n=== Test: Logout Admin ===");

        getAuthRequest(adminToken)
                .when()
                .get("/users/logout")
                .then()
                .statusCode(200);

        System.out.println("Admin logged out successfully");
    }

    @Description("Get Specific User by ID With User Token")
    @Test(priority = 6)
    public void testGetSpecificUserByUserTokenNegative() {

        System.out.println("\n=== Test: Get Specific User by ID With User Token (Negative) ===");

        Response response = getAuthRequest(userToken)
                .when()
                .get("/users/1");
        int statusCode = response.getStatusCode();
        response.prettyPrint();
        if (statusCode == 401) {
            System.out.println("Unauthorized access");
        } else if (statusCode == 200) {
            System.out.println("BUG:Normal user can access other users' data — access control missing!");
        }
    }

    @Description("DELETE User Without Admin Token ")
    @Test(priority = 7)
    public void testDeleteUserwithoutToken() {
        System.out.println("\n=== Test: Delete User (Admin) ===");

        Response response = getAuthRequest(userToken)
                .when()
                .delete("/users/2");

        int statusCode = response.getStatusCode();
        if (statusCode == 401) {
            System.out.println("Correctly rejected delete without admin token");
        }

    }

    @BeforeClass
    protected void preCondition() {
        testData = new JsonReader("user-data");
    }
}
