package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;



@Test
public class BookingApiTest2 {


    //POJO class to deserilize bookingIds from getBookingIds
    public static class BookingId {
        private int bookingid;

        public int getBookingid() {
            return bookingid;
        }

        public void setBookingid(int bookingid) {
            this.bookingid = bookingid;
        }
    }

    public static class Booking {
        private String firstname;
        private String lastname;
        private Integer totalprice;
        private Boolean depositpaid;
        private BookingDates bookingdates;
        private String additionalneeds;

        public String getFirstname() {
            return firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public int getTotalprice() {
            return totalprice;
        }


        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public void setTotalprice(Integer totalprice) {
            this.totalprice = totalprice;
        }

        public void setDepositpaid(Boolean depositpaid) {
            this.depositpaid = depositpaid;
        }

        public void setBookingdates(BookingDates bookingdates) {
            this.bookingdates = bookingdates;
        }

        public void setAdditionalneeds(String additionalneeds) {
            this.additionalneeds = additionalneeds;
        }

        public String getAdditionalneeds() {
            return additionalneeds;
        }

        public BookingDates getBookingdates() {
            return bookingdates;
        }

        public boolean isDepositpaid() {
            return depositpaid;
        }


    }

    public static class Credentials {
        @JsonProperty("username")
        private String username = "admin";
        @JsonProperty("password")
        private String password = "password123";

    }

    public static class BookingDates {
        private String checkin;
        private String checkout;

        public String getCheckin() {
            return checkin;
        }

        public String getCheckout() {
            return checkout;
        }

        public void setCheckin(String checkin) {
            this.checkin = checkin;
        }

        public void setCheckout(String checkout) {
            this.checkout = checkout;
        }
    }

    public Booking createBooking() {
        Booking booking = new Booking();
        booking.setFirstname("Jim");
        booking.setLastname("Brown");
        booking.setTotalprice(111);
        booking.setDepositpaid(true);

        BookingDates bookingDates = new BookingDates();
        bookingDates.setCheckin("2018-01-01");
        bookingDates.setCheckout("2019-01-01");
        booking.setBookingdates(bookingDates);

        booking.setAdditionalneeds("Breakfast");

        return booking;
    }


    @BeforeTest
    public void CreateToken(ITestContext context){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RequestSpecification request = RestAssured.given();

        // Create booking object
        Credentials credentials = new Credentials();

        Response response = request
                .header("Content-Type","application/json")
                .body(credentials)
                .log()
                .all()
                .post("/auth")
                .then()
                .log()
                .all()
                .extract()
                .response();

        assertEquals(response.statusCode(), 200);

        // Deserialize response into String token
        String token = response.jsonPath().getString("token");
        // Save the token in the context if needed
        context.setAttribute("token", token);

    }


    public void GetBookingIds(ITestContext context){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RequestSpecification request = RestAssured.given();

        Response response = request
                .header("Content-Type","application/json")
                .get("/booking")
                .then()
                .log()
                .all()
                .extract()
                .response();

        assertEquals(response.statusCode(), 200 );
        //"." because there is no data: or bookings:
        List<BookingId> bookingIds = response.jsonPath().getList(".", BookingId.class);
        for (BookingId bookingId : bookingIds) {
            assertNotNull(bookingId);
            assertNotNull(bookingId.getBookingid());
            System.out.println("bookingId:" + bookingId.getBookingid());
        }

        // Save the bookingId in position 0 to the context
        if (!bookingIds.isEmpty()) {
            int bookingId = bookingIds.get(0).getBookingid();
            context.setAttribute("bookingId", bookingId);
            System.out.println("Saved bookingId: " + bookingId);
        }
    }

    @Test(dependsOnMethods = "GetBookingIds")
    public void GetBooking(ITestContext context){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RequestSpecification request = RestAssured.given();

        Object bookingIdObj = context.getAttribute("bookingId");
        int bookingId = (int) bookingIdObj;

        Response response = request
                .header("Content-Type","application/json")
                .get("/booking/"+bookingId)
                .then()
                .log()
                .all()
                .extract()
                .response();

        assertEquals(response.statusCode(), 200);

        // Deserialize response into BookingResponse
        Booking bookingResponse = response.as(Booking.class);

        // Assert each field
        assertNotNull(bookingResponse.getFirstname());
        assertNotNull(bookingResponse.getLastname());
    }

    @Test(dependsOnMethods = "GetBooking")
    public void CreateBooking(ITestContext context){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RequestSpecification request = RestAssured.given();

        // Create booking object
        Booking newBooking = createBooking();

        Response response = request
                .header("Content-Type","application/json")
                .body(newBooking)
                .post("/booking")
                .then()
                .log()
                .all()
                .extract()
                .response();

        assertEquals(response.statusCode(), 200);

        // Deserialize response into BookingResponse
        Booking bookingResponse = response.jsonPath().getObject("booking",Booking.class);

        // Store the created booking ID in the context
        int bookingId = response.jsonPath().getInt("bookingid");
        context.setAttribute("bookingId", bookingId);

        // Assert each field
        assertNotNull(bookingResponse.getFirstname());
        assertNotNull(bookingResponse.getLastname());
        assertEquals(bookingResponse.getFirstname(), newBooking.getFirstname());
        assertEquals(bookingResponse.getLastname(), newBooking.getLastname());
        assertEquals(bookingResponse.getTotalprice(), newBooking.getTotalprice());
        assertEquals(bookingResponse.isDepositpaid(), newBooking.isDepositpaid());
        assertEquals(bookingResponse.getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(bookingResponse.getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(bookingResponse.getAdditionalneeds(), newBooking.getAdditionalneeds());
    }







}







