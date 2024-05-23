package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import static io.restassured.path.json.JsonPath.given;
import static org.testng.Assert.assertEquals;

class RestAssuredUtil {

    public static Response sendRequest(String baseUri, String basePath, Method method, String body) {
        RestAssured.baseURI = baseUri;

        RequestSpecification request =  RestAssured.given()
                .log()
                .all()
                .basePath(basePath)
                .contentType("application/json");

        if (body != null && !body.isEmpty()) {
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

    public static long extractBookingId(Response response) {
        return response
                .jsonPath()
                .getLong("bookingid");
    }
}

class Tokencreds {
    private String username;
    private String password;

    //Constructor
    public Tokencreds(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //Getter
    public String getUsername() {
        return username;
    }

    //Getter
    public String getPassword() {
        return password;
    }
}

class TokenBuilder {

    public static String getToken() {
        Tokencreds tokenCreds = new Tokencreds("admin", "password123");
        return RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://restful-booker.herokuapp.com")
                .basePath("/auth")
                .contentType(ContentType.JSON)
                .body("{ \"username\" : \"" + tokenCreds.getUsername() + "\", \"password\" : \"" + tokenCreds.getPassword() + "\" }")
                .when()
                .post()
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getString("token");
    }
}


public class BookingApiTest {

    @Test
    public void postBookingTest(ITestContext context) {
        String baseUri = "https://restful-booker.herokuapp.com";
        String basePath = "/booking";
        String requestBody = "{\"firstname\":\"Jim\",\"lastname\":\"Brown\",\"totalprice\":111,\"depositpaid\":true,\"bookingdates\":{\"checkin\":\"2018-01-01\",\"checkout\":\"2019-01-01\"},\"additionalneeds\":\"Breakfast\"}";

        Response response = RestAssuredUtil.sendRequest(baseUri, basePath, Method.POST, requestBody);

        System.out.println("Status received => " + response.getStatusLine());
        System.out.println("Response => " + response.prettyPrint());

        assertEquals(200, response.getStatusCode(), "Correct status code returned");

        long bookingId = response.jsonPath().getLong("bookingid");
        context.setAttribute("bookingId", bookingId);

        // Logging the petId
        System.out.println("Booking ID created: " + bookingId);

        // Storing data in a context to use for other tests
        context.setAttribute("bookingid", bookingId);
        System.out.println("Context attribute 'bookingid' set to: " + context.getAttribute("bookingid"));
    }

    @Test(dependsOnMethods = "postBookingTest")
    public void getBookingTest(ITestContext context) {
        // Retrieving the bookingId from the context
        Object bookingIdObj = context.getAttribute("bookingid");
        if (bookingIdObj == null) {
            System.err.println("petId is not set in the context");
            throw new NullPointerException("bookingId is not set in the context");
        }

        long bookingId = (long) bookingIdObj;

        // Performing a GET request to retrieve the pet information
        RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://restful-booker.herokuapp.com")
                .basePath("/booking/" + bookingId)
                .contentType(ContentType.JSON)
                .when()
                .get()
                .then()
                .log()
                .all()
                .statusCode(200); // Asserting that the status code is 200

        System.out.println("Retrieved pet with ID: " + bookingId);
    }

    @Test(dependsOnMethods = "getBookingTest")
    public void putBookingTest(ITestContext context) {

        // Retrieving the bookingId from the context
        Object bookingIdObj = context.getAttribute("bookingid");
        if (bookingIdObj == null) {
            System.err.println("petId is not set in the context");
            throw new NullPointerException("bookingId is not set in the context");
        }

        long bookingId = (long) bookingIdObj;
        // Getting the token using TokenBuilder
        String token = TokenBuilder.getToken();

        // Sample request body for updating a booking
        String requestBody = "{\n" +
                "    \"firstname\" : \"James\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        RestAssured
                .given()
                .log()
                .all()
                .baseUri("https://restful-booker.herokuapp.com")
                .basePath("/booking/" + bookingId)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestBody)
                .when()
                .put()
                .then()
                .log()
                .all()
                .statusCode(200);

        System.out.println("Booking ID updated: " + bookingId);
    }
}

