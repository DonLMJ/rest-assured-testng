package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class PetAPITest {

    @Test
    public void postPetTest(ITestContext context) {
        long petId = RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet")
                .contentType(ContentType.JSON)
                .body("{\"id\":0,\"category\":{\"id\":0,\"name\":\"string\"},\"name\":\"doggie\",\"photoUrls\":[\"string\"],\"tags\":[{\"id\":0,\"name\":\"string\"}],\"status\":\"available\"}")
                .when()
                .post()
                .then()
                .log()
                .all()
                .statusCode(200) // Asserting that the status code is 200
                .extract()
                .jsonPath()
                .getLong("id");

        // Logging the petId
        System.out.println("Pet ID created: " + petId);

        // Storing data in a context to use for other tests
        context.setAttribute("id", petId);
        System.out.println("Context attribute 'id' set to: " + context.getAttribute("id"));
    }

    @Test(dependsOnMethods = "postPetTest")
    public void getPetTest(ITestContext context) {
        // Retrieving the petId from the context
        Object petIdObj = context.getAttribute("id");
        if (petIdObj == null) {
            System.err.println("petId is not set in the context");
            throw new NullPointerException("petId is not set in the context");
        }

        long petId = (long) petIdObj;

        // Performing a GET request to retrieve the pet information
        RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet/" + petId)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .log()
                .all()
                .statusCode(200); // Asserting that the status code is 200

        System.out.println("Retrieved pet with ID: " + petId);
    }

    @Test(dependsOnMethods = "getPetTest")
    public void putPetTest(ITestContext context) {
        long petId = RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet")
                .contentType(ContentType.JSON)
                .body("{\"id\":0,\"category\":{\"id\":0,\"name\":\"string\"},\"name\":\"doggie\",\"photoUrls\":[\"string\"],\"tags\":[{\"id\":0,\"name\":\"string\"}],\"status\":\"available\"}")
                .when()
                .put()
                .then()
                .log()
                .all()
                .statusCode(200) // Asserting that the status code is 200
                .extract()
                .jsonPath()
                .getLong("id");

        // Logging the petId
        System.out.println("Pet ID updated: " + petId);

        // Storing data in a context to use for other tests
        context.setAttribute("id", petId);
        System.out.println("Context attribute 'id' set to: " + context.getAttribute("id"));
    }

    @Test(dependsOnMethods = "putPetTest")
    public void deletePetTest(ITestContext context) {
        Object petIdObj = context.getAttribute("id");
        if (petIdObj == null) {
            System.err.println("petId is not set in the context");
            throw new NullPointerException("petId is not set in the context");
        }

        long petId = (long) petIdObj;

        RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet/" + petId)
                .contentType(ContentType.JSON)
                .when()
                .delete()
                .then()
                .log()
                .all()
                .statusCode(200); // Asserting that the status code is 200

        System.out.println("Deleted pet with ID: " + petId);

    }

    @Test(dependsOnMethods = "deletePetTest")
    public void getPetTestNotFound(ITestContext context) {
        // Retrieving the petId from the context
        Object petIdObj = context.getAttribute("id");
        if (petIdObj == null) {
            System.err.println("petId is not set in the context");
            throw new NullPointerException("petId is not set in the context");
        }

        long petId = (long) petIdObj;

        // Performing a GET request to retrieve the pet information
        RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2/pet/" + petId)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .log()
                .all()
                .statusCode(404); // Asserting that the status code is 200

        System.out.println("Retrieved pet with ID: " + petId);
    }

}
