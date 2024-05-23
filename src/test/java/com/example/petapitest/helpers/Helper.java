package com.example.petapitest.helpers;

import com.example.petapitest.Category;
import com.example.petapitest.Pet;
import com.example.petapitest.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Helper {
    public static Pet createPet(){
        Pet pet = new Pet();
        pet.setId(0);
        Category category = new Category();
        category.setId(0);
        category.setName("string");
        pet.setCategory(category);
        pet.setName("doggie");
        pet.setPhotoUrls(new String[]{"string"});
        Tag tag = new Tag();
        tag.setId(0);
        tag.setName("string");
        pet.setTags(new Tag[]{tag});
        pet.setStatus("available");

        return pet;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeObjectToJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static Response sendRequest(String basePath, Method method, Object body) {
        RestAssured.baseURI ="https://petstore.swagger.io";



        RequestSpecification request =  RestAssured.given()
                .log()
                .all()
                .basePath(basePath)
                .contentType("application/json");

        if (body != null && !body.toString().isEmpty()) {
            request.body(body);
        }

        Response response = null;
        switch (method) {
            case GET:
                response = request.get();
                break;
            case POST:
                response = request.post();
                break;
            case PUT:
                response = request.put();
                break;
            case DELETE:
                response = request.delete();
                break;
            // Add more cases for other HTTP methods if needed
            default:
                throw new UnsupportedOperationException("HTTP method not supported");
        }

        return response;
    }
}
