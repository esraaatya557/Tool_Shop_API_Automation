package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("TOOL SHOP")
@Feature("Category And Brand Management")
@Severity(SeverityLevel.CRITICAL)
@Owner("Esraa Atya")
@Test(groups = "CatgoryAndBrand")
public class CategoryAndBrandModuleTest extends BaseTest {

    @Description("Get All Categories")
    @Test(priority = 1)
    public void testGetAllCategories() {
        System.out.println("\n=== Test: Get All Categories ===");

        Response response = getRequest()
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("size()", greaterThan(0))
                .extract().response();

        categoryId = response.jsonPath().getInt("[0].id");
        categoryName = response.jsonPath().getString("[0].name");
        int totalCategories = response.jsonPath().getList("$").size();

        System.out.println("Categories retrieved successfully");
        System.out.println("Total categories: " + totalCategories);
        System.out.println("First category: " + categoryName);
        System.out.println("Saved category ID: " + categoryId);
    }

    @Description("Get Category by ID")
    @Test(priority = 2, dependsOnMethods = "testGetAllCategories")
    public void testGetCategoryById() {
        System.out.println("\n=== Test: Get Category by ID ===");

        Response response = getRequest()
                .when()
                .get("/categories/" + categoryId)
                .then()
                .statusCode(200)
                .body("id", equalTo(categoryId))
                .body("name", notNullValue())
                .body("slug", notNullValue())
                .extract().response();

        String categoryName = response.jsonPath().getString("name");
        String slug = response.jsonPath().getString("slug");

        System.out.println("Category retrieved successfully");
        System.out.println("Category: " + categoryName);
        System.out.println("Slug: " + slug);
    }

    @Description("Negative Test: Get Category with Invalid ID")
    @Test(priority = 3)
    public void testGetCategoryWithInvalidId() {
        System.out.println("\n=== Negative Test: Get Category with Invalid ID ===");

        getRequest()
                .when()
                .get("/categories/99999")
                .then()
                .statusCode(404);

        System.out.println("Correctly returned 404 for invalid category ID");
    }


    @Description("Get All Brands")
    @Test(priority = 4)
    public void testGetAllBrands() {
        System.out.println("\n=== Test: Get All Brands ===");

        Response response = getRequest()
                .when()
                .get("/brands")
                .then()
                .statusCode(200)
                .body("$", instanceOf(java.util.List.class))
                .body("size()", greaterThan(0))
                .extract().response();

        brandId = response.jsonPath().getInt("[0].id");
        String brandName = response.jsonPath().getString("[0].name");
        int totalBrands = response.jsonPath().getList("$").size();

        System.out.println("Brands retrieved successfully");
        System.out.println("Total brands: " + totalBrands);
        System.out.println("First brand: " + brandName);
        System.out.println("Saved brand ID: " + brandId);
    }

    @Description("Get Brand by ID")
    @Test(priority = 5, dependsOnMethods = "testGetAllBrands")
    public void testGetBrandById() {
        System.out.println("\n=== Test: Get Brand by ID ===");

        Response response = getRequest()
                .when()
                .get("/brands/" + brandId)
                .then()
                .statusCode(200)
                .body("id", equalTo(brandId))
                .body("name", notNullValue())
                .body("slug", notNullValue())
                .extract().response();

        String brandName = response.jsonPath().getString("name");
        String slug = response.jsonPath().getString("slug");

        System.out.println("Brand retrieved successfully");
        System.out.println("Brand: " + brandName);
        System.out.println("Slug: " + slug);
    }

    @Description("Negative Test: Get Brand with Invalid ID")
    @Test(priority = 6)
    public void testGetBrandWithInvalidId() {
        System.out.println("\n=== Negative Test: Get Brand with Invalid ID ===");

        getRequest()
                .when()
                .get("/brands/99999")
                .then()
                .statusCode(404);

        System.out.println("Correctly returned 404 for invalid brand ID");
    }

}