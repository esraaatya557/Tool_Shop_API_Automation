package tests;

import Config.ConfigReader;
import Config.JsonReader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("TOOL SHOP")
@Feature("Products Management")
@Severity(SeverityLevel.CRITICAL)
@Owner("Esraa Atya")
@Test(groups = "product")
public class ProductModuleTest extends BaseTest {

    @Description("Get All Products")
    @Test(priority = 1)
    public void testGetAllProducts() {
        System.out.println("\n=== Test: Get All Products ===");

        Response response = getRequest()
                .queryParam("sort", "id")
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .body("data", hasSize(greaterThan(0)))
                .body("current_page", equalTo(1))
                .extract().response();

        productId = response.jsonPath().getInt("data[0].id");
        productName = response.jsonPath().getString("data[0].name");
        categoryId = response.jsonPath().getInt("data[0].category_id");
        brandId = response.jsonPath().getInt("data[0].brand_id");

        int totalProducts = response.jsonPath().getInt("total");
        System.out.println("Retrieved products successfully");
        System.out.println("Total products: " + totalProducts);
        System.out.println("Saved product ID: " + productId);
    }

    @Description("Create Product")
    @Test(priority = 2, dependsOnMethods = "testGetAllProducts")
    public void testCreateProduct() {
        System.out.println("\n=== Test: Create Product ===");


        String requestBody = "{\n" +
                "\"name\": \"" + testData.getJsonData("name") + "\",\n" +
                "\"description\": \"" + testData.getJsonData("description") + "\",\n" +
                "\"price\": " + testData.getJsonData("price") + ",\n" +
                "\"category_id\": " + testData.getJsonData("category_id") + ",\n" +
                "\"brand_id\":" + testData.getJsonData("brand_id") + ",\n" +
                "\"product_image_id\":" + testData.getJsonData("product_image_id") + ",\n" +
                "\"is_location_offer\": " + testData.getJsonData("is_location_offer") + ",\n" +
                "\"is_rental\": " + testData.getJsonData("is_rental") + ",\n" +
                "\"co2_rating\": \"" + testData.getJsonData("co2_rating") + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", containsString("Electric Drill 600W"))
                .extract().response();

        createdProductId = response.jsonPath().getInt("id");
        System.out.println("Product created successfully");
        System.out.println("Product ID: " + createdProductId);
    }

    @Description("Get Product by ID")
    @Test(priority = 3, dependsOnMethods = "testGetAllProducts")
    public void testGetProductById() {
        System.out.println("\n=== Test: Get Product by ID ===");

        Response response = getRequest()
                .when()
                .get("/products/" + productId)
                .then()
                .statusCode(200)
                .body("id", equalTo(productId))
                .body("name", equalTo(productName))
                .extract().response();

        double productPrice = response.jsonPath().getDouble("price");
        System.out.println("Product retrieved successfully");
        System.out.println(productName + " - $" + productPrice);
    }

    @Description("Update Product")
    @Test(priority = 4, dependsOnMethods = "testCreateProduct")
    public void testUpdateProduct() {
        System.out.println("\n=== Test: Update Product ===");

        String requestBody = "{\n" +
                "\"name\": \"" + testData.getJsonData("updatedName") + "\",\n" +
                "\"description\": \"" + testData.getJsonData("description") + "\",\n" +
                "\"price\": " + testData.getJsonData("updatedPrice") + ",\n" +
                "\"category_id\": " + testData.getJsonData("category_id") + ",\n" +
                "\"brand_id\":" + testData.getJsonData("brand_id") + ",\n" +
                "\"product_image_id\":" + testData.getJsonData("product_image_id") + ",\n" +
                "\"is_location_offer\": " + testData.getJsonData("is_location_offer") + ",\n" +
                "\"is_rental\": " + testData.getJsonData("is_rental") + ",\n" +
                "\"co2_rating\": \"" + testData.getJsonData("co2_rating") + "\"\n" +
                "}";

        getRequest()
                .body(requestBody)
                .when()
                .put("/products/" + createdProductId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));

        System.out.println("Product updated successfully");
    }

    @Description("Get Related Products")
    @Test(priority = 5, dependsOnMethods = "testGetAllProducts")
    public void testGetRelatedProducts() {
        System.out.println("\n=== Test: Get Related Products ===");

        Response response = getRequest()
                .when()
                .get("/products/" + productId + "/related")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .extract().response();

        int relatedCount = response.jsonPath().getList("$").size();
        System.out.println("Related products retrieved");
        System.out.println("Found " + relatedCount + " related products");
    }

    @Description("Search Products")
    @Test(priority = 6)
    public void testSearchProducts() {
        System.out.println("\n=== Test: Search Products ===");

        Response response = getRequest()
                .queryParam("q", testData.getJsonData("searchProduct"))
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("data", notNullValue())
                .extract().response();

        int resultsCount = response.jsonPath().getList("data").size();
        System.out.println("Search completed");
        System.out.println("Found " + resultsCount +
                " products matching '" + testData.getJsonData("searchProduct") + "'");
    }

    @Description("BUG TEST: Create Product with Negative Price")
    @Test(priority = 7)
    public void testCreateProductWithNegativePrice() {
        System.out.println("\n=== BUG TEST: Create Product with Negative Price ===");

        String requestBody = "{\n" +
                "\"name\": \"" + testData.getJsonData("updatedName") + "\",\n" +
                "\"description\": \"" + testData.getJsonData("description") + "\",\n" +
                "\"price\": " + testData.getJsonData("negativePrice") + ",\n" +
                "\"category_id\": " + testData.getJsonData("category_id") + ",\n" +
                "\"brand_id\":" + testData.getJsonData("brand_id") + ",\n" +
                "\"product_image_id\":" + testData.getJsonData("product_image_id") + ",\n" +
                "\"is_location_offer\": " + testData.getJsonData("is_location_offer") + ",\n" +
                "\"is_rental\": " + testData.getJsonData("is_rental") + ",\n" +
                "\"co2_rating\": \"" + testData.getJsonData("co2_rating") + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/products");

        int statusCode = response.getStatusCode();
        if (statusCode == 201) {
            double createdPrice = response.jsonPath().getDouble("price");
            buggyProductId = response.jsonPath().getInt("id");

            System.out.println("BUG DETECTED: API accepted negative price!");
            System.out.println("Created product with price: $" + createdPrice);
            System.out.println("Expected: 400/422 with validation error");

        }
    }

    @Description("Delete Product (Admin)")
    @Test(priority = 8, dependsOnMethods = {"testCreateProductWithNegativePrice"})
    public void testDeleteProduct() {
        System.out.println("\n=== Test: Delete Product (Admin) ===");

        String requestBody = "{\n" +
                "  \"email\": \"" + ConfigReader.getAdminEmail() + "\",\n" +
                "  \"password\": \"" + ConfigReader.getAdminPassword() + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/users/login");
        adminToken = response.jsonPath().getString("access_token");

        getAuthRequest(adminToken)
                .when()
                .delete("/products/" + buggyProductId)
                .then()
                .statusCode(204);

        System.out.println("Product deleted successfully");
        System.out.println("Deleted product ID: " + buggyProductId);
    }

    @Description("Create Product with Missing Fields (Negative)")
    @Test(priority = 9)
    public void testCreateProductwithMissingFields() {
        System.out.println("\n=== Create Product with Missing Fields (Negative) ===");

        String requestBody = "{\n" +
                "\"name\": \"" + testData.getJsonData("updatedName") + "\",\n" +
                "\"description\": \"" + testData.getJsonData("description") + "\",\n" +
                "\"price\": " + testData.getJsonData("negativePrice") + ",\n" +
                "\"brand_id\":" + testData.getJsonData("brand_id") + ",\n" +
                "\"product_image_id\":" + testData.getJsonData("product_image_id") + ",\n" +
                "\"is_location_offer\": " + testData.getJsonData("is_location_offer") + ",\n" +
                "\"is_rental\": " + testData.getJsonData("is_rental") + ",\n" +
                "\"co2_rating\": \"" + testData.getJsonData("co2_rating") + "\"\n" +
                "}";

        Response response = getRequest()
                .body(requestBody)
                .when()
                .post("/products");
        int statusCode = response.getStatusCode();
        if (statusCode == 422) {
            System.out.println("Correctly rejected incomplete product");
        }
    }

    @Description("Get Product with Invalid ID")
    @Test(priority = 10)
    public void testGetProductWithInvalidId() {
        System.out.println("\n=== Negative Test: Get Product with Invalid ID ===");

        getRequest()
                .when()
                .get("/products/954")
                .then()
                .statusCode(404);

        System.out.println("Correctly returned 404 for invalid product ID");
    }

    @BeforeClass
    protected void preCondition() {
        testData = new JsonReader("product-data");
    }
}