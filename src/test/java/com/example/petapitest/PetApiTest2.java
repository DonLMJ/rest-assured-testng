package com.example.petapitest;

import com.example.petapitest.helpers.Helper;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.Method.POST;
import static io.restassured.http.Method.PUT;


public class PetApiTest2 {

    @Test
    public void UpdateExistingPet(ITestContext context) {
        Pet pet = Helper.createPet();


        Response responsePost = Helper.sendRequest("/v2/pet",POST,pet);



        Pet newPet = responsePost.jsonPath().getObject("", Pet.class);

        Long id = newPet.getId();
        // Logging the petId
        System.out.println("Pet ID created: " + id);
        Assert.assertEquals(responsePost.statusCode(), 200);

        //Change to created pet
        newPet.setName("ChangedName");

        Response responsePut = Helper.sendRequest("/v2/pet",PUT,newPet);
        Pet updatePet = responsePut.jsonPath().getObject("", Pet.class);

        Assert.assertEquals(updatePet.getId(), newPet.getId());
        Assert.assertEquals(updatePet.getName(), "ChangedName");
    }


    @Test
    public void testAddNewPet() {
        String newPet = "{\"id\": 0, \"name\": \"Rex\", \"status\": \"available\"}";

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                //.header("Content-Type", "application/json")
                //.header("Accept", "application/json")
                //.header("Cookie", "token=" + token)
                .body(newPet)
                .post("https://petstore.swagger.io/v2/pet");

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("name"), "Rex");
    }
}
