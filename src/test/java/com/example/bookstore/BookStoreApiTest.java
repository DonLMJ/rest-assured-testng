package com.example.bookstore;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

@Test
public class BookStoreApiTest {

    public void getBooksTest(){
        RestAssured.baseURI = "https://demoqa.com/BookStore";

        Response response = given().log().all().when().get("/v1/Books").then().log().all()
                .extract().response();

        assertEquals(200, response.statusCode());

        String contentType = response.header("Content-Type");
        assertEquals(contentType /* actual value */, "application/json; charset=utf-8" /* expected value */);
    }

    public void postBookTest(){
        RestAssured.baseURI = "https://demoqa.com/BookStore";



        Response response = given().body("").log().all().when().post("/v1/Books").then().log().all()
                .extract().response();

        assertEquals(200, response.statusCode());

        String contentType = response.header("Content-Type");
        assertEquals(contentType /* actual value */, "application/json; charset=utf-8" /* expected value */);
    }
}
