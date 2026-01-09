package tests;

import Config.JsonReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("TOOL SHOP")
@Feature("Invoice Management")
@Severity(SeverityLevel.CRITICAL)
@Owner("Esraa Atya")
@Test(groups = "invoice")
public class InvoiceModuleTest extends BaseTest {

    @BeforeClass
    public void setupInvoiceTests() {
        testData = new JsonReader("invoice-data");
        String loginBody = "{\n" +
                "  \"email\": \"" + testData.getJsonData("registeredUser.email") + "\",\n" +
                "  \"password\": \"" + testData.getJsonData("registeredUser.password") + "\"\n" +
                "}";
        Response response = getRequest()
                .body(loginBody)
                .post("/users/login");
        userToken = response.jsonPath().getString("access_token");
        System.out.println("User logged in for invoice tests");


    }

    @Description("Create Invoice")
    @Test(priority = 1)
    public void testCreateInvoice() {
        System.out.println("\n=== Test: Create Invoice ===");

        String requestBody = "{\n" +
                "  \"user_id\":" + testData.getJsonData("invoice.user_id") + ",\n" +
                "  \"billing_address\": \"" + testData.getJsonData("invoice.billing_address") + "\",\n" +
                "  \"billing_city\": \"" + testData.getJsonData("invoice.billing_city") + "\",\n" +
                "  \"billing_country\": \"" + testData.getJsonData("invoice.billing_country") + "\",\n" +
                "  \"billing_state\": \"" + testData.getJsonData("invoice.billing_state") + "\",\n" +
                "  \"billing_postcode\": \"" + testData.getJsonData("invoice.billing_postcode") + "\",\n" +
                "  \"total\": " + testData.getJsonData("invoice.total") + ",\n" +
                "  \"payment_method\": \"" + testData.getJsonData("invoice.payment_method") + "\",\n" +
                "  \"payment_account_name\": \"" + testData.getJsonData("invoice.payment_account_name") + "\",\n" +
                "  \"payment_account_number\": \"" + testData.getJsonData("invoice.payment_account_number") + "\",\n" +
                "  \"invoice_items\": [\n" +
                "    {\n" +
                "      \"product_id\": " + testData.getJsonData("invoice.invoice_items[0].product_id") + ",\n" +
                "      \"quantity\": " + testData.getJsonData("invoice.invoice_items[0].quantity") + ",\n" +
                "      \"unit_price\":" + testData.getJsonData("invoice.invoice_items[0].unit_price") + "\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        Response response = getAuthRequest(userToken)
                .body(requestBody)
                .when()
                .post("/invoices")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("invoice_number", notNullValue())
                .body("total", notNullValue())
                .extract().response();
        System.out.println(response.prettyPrint());

        invoiceId = response.jsonPath().getInt("id");
        invoiceNumber = response.jsonPath().getString("invoice_number");
        double total = response.jsonPath().getDouble("total");

        System.out.println("Invoice created successfully");
        System.out.println("Invoice: " + invoiceNumber);
        System.out.println("Total: $" + total);
        System.out.println("Invoice ID: " + invoiceId);
    }

    @Description("Get All Invoices")
    @Test(priority = 2, dependsOnMethods = "testCreateInvoice")
    public void testGetAllInvoices() {
        System.out.println("\n=== Test: Get All Invoices ===");

        Response response = getAuthRequest(userToken)
                .when()
                .get("/invoices")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data", instanceOf(java.util.List.class))
                .extract().response();

        int totalInvoices = response.jsonPath().getList("data").size();
        java.util.List<Integer> userIds = response.jsonPath().getList("data.user_id");
        java.util.Set<Integer> uniqueUsers = new java.util.HashSet<>(userIds);

        System.out.println("Invoices retrieved");
        System.out.println("Total invoices: " + totalInvoices);

        if (uniqueUsers.size() > 1) {
            System.out.println("BUG DETECTED: Found invoices from " + uniqueUsers.size() + " different users!");
            System.out.println("Security Issue: User can see other users' invoices");
        } else {
            System.out.println("All invoices belong to current user");
        }
    }

    @Description("Update specific invoice by ID")
    @Test(priority = 3, dependsOnMethods = "testCreateInvoice")
    public void testUpdateInvoice() {
        System.out.println("\n=== Test: Update specific invoice by ID ===");

        String requestBody = "{\n" +
                "  \"user_id\":" + testData.getJsonData("invoice.user_id") + ",\n" +
                "  \"billing_address\": \"" + testData.getJsonData("invoice.billing_address") + "\",\n" +
                "  \"billing_city\": \"" + testData.getJsonData("invoice.billing_city") + "\",\n" +
                "  \"billing_country\": \"" + testData.getJsonData("invoice.billing_country") + "\",\n" +
                "  \"billing_state\": \"" + testData.getJsonData("invoice.billing_state") + "\",\n" +
                "  \"billing_postcode\": \"" + testData.getJsonData("invoice.update_billing_postcode") + "\",\n" +
                "  \"total\": " + testData.getJsonData("invoice.total") + "\n" +
                "\n" +
                "}";

        Response response = getAuthRequest(userToken)
                .body(requestBody)
                .when()
                .put("/invoices/" + invoiceId)
                .then()
                .statusCode(200)
                .extract().response();

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {
            System.out.println("Invoice status updated successfully");
        } else {
            System.out.println("Unexpected status code: " + statusCode);
        }
    }

    @Description("Get Invoice by ID")
    @Test(priority = 4, dependsOnMethods = "testCreateInvoice")
    public void testGetInvoiceById() {
        System.out.println("\n=== Test: Get Invoice by ID ===");

        Response response = getAuthRequest(userToken)
                .when()
                .get("/invoices/" + invoiceId)
                .then()
                .statusCode(200)
                .body("id", equalTo(invoiceId))
                .body("invoice_number", equalTo(invoiceNumber))
                .body("billing_address", equalTo(testData.getJsonData("invoice.billing_address")))
                .body("payment_method", equalTo(testData.getJsonData("invoice.payment_method")))
                .extract().response();

        String paymentMethod = response.jsonPath().getString("payment_method");
        System.out.println("Invoice retrieved successfully");
        System.out.println("Invoice: " + invoiceNumber);
        System.out.println("Payment: " + paymentMethod);

    }

    @Description("Create Invoice Without Token")
    @Test(priority = 5)
    public void testCreateInvoiceWithoutToken() {
        System.out.println("\n=== Test: Create Invoice Without Token===");

        String requestBody = "{\n" +
                "  \"user_id\":" + testData.getJsonData("invoice.user_id") + ",\n" +
                "  \"billing_address\": \"" + testData.getJsonData("invoice.billing_address") + "\",\n" +
                "  \"billing_city\": \"" + testData.getJsonData("invoice.billing_city") + "\",\n" +
                "  \"billing_country\": \"" + testData.getJsonData("invoice.billing_country") + "\",\n" +
                "  \"billing_state\": \"" + testData.getJsonData("invoice.billing_state") + "\",\n" +
                "  \"billing_postcode\": \"" + testData.getJsonData("invoice.billing_postcode") + "\",\n" +
                "  \"total\": " + testData.getJsonData("invoice.total") + ",\n" +
                "  \"payment_method\": \"" + testData.getJsonData("invoice.payment_method") + "\",\n" +
                "  \"payment_account_name\": \"" + testData.getJsonData("invoice.payment_account_name") + "\",\n" +
                "  \"payment_account_number\": \"" + testData.getJsonData("invoice.payment_account_number") + "\",\n" +
                "  \"invoice_items\": [\n" +
                "    {\n" +
                "      \"product_id\": " + testData.getJsonData("invoice.invoice_items.product_id") + ",\n" +
                "      \"quantity\": " + testData.getJsonData("invoice.invoice_items.quantity") + ",\n" +
                "      \"unit_price\":" + testData.getJsonData("invoice.invoice_items.unit_price") + "\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/invoices");

        int statusCode = response.getStatusCode();
        if (statusCode == 401) {
            System.out.println("Correctly rejected request without token");
        }
    }


}