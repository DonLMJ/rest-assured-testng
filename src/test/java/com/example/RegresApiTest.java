package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class RegresApiTest {



    @Test
    public void getUsersTest(){

        RestAssured.baseURI = "https://reqres.in";

        Response response = given().queryParam ("page", 2).log().all().when().get("api/users").then().log().all()
                .extract().response();

        assertEquals(200, response.statusCode());

        //Getting value of a respective field from response
        JsonPath jsonPath = response.jsonPath ();
        assertEquals(jsonPath.getInt ("page"), 2);

        //List all objects inside the array
        List<String > dataArray = jsonPath.getList ("data");
        System.out.println ("Data array " +dataArray);

        //Listing first object values
        System.out.println (jsonPath.getJsonObject ("data[0]").toString ());

        //listing specific field values of all objects inside the array
        List<String> listOfFirstNames = jsonPath.getList ("data.first_name");
        System.out.println ("List of first names in data array " +listOfFirstNames);

        String firstNameInSecondObject = jsonPath.getString ("data[1].first_name");
        System.out.println ("First Name in second object " +firstNameInSecondObject);
        assertEquals (firstNameInSecondObject, "Lindsay");



    }

    @Test
    public void postUserTest(){

        RestAssured.baseURI = "https://reqres.in";

        String requestBody = "{\n" +
                "    \"email\": \"eve.holt@reqres.in\",\n" +
                "    \"password\": \"cityslicka\"\n" +
                "}";

        Response response = given().contentType(ContentType.JSON).body(requestBody).log().all().when().post("api/users").then().log().all()
                .extract().response();

        assertEquals(201, response.statusCode());

    }
}
