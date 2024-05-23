package com.example.bookstore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.codec.binary.Base64;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class BookStoreApiTest2 {

     class UserCredentials {
        private String UserName;
        private String Password;

        // Constructors
        public UserCredentials(String userName, String password) {
            this.UserName = userName;
            this.Password = password;
        }

        // Getters and Setters
        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String password) {
            Password = password;
        }
    }

    public String generatePassword() {
        Faker faker = new Faker();
        String password;
        do {
            password = faker.internet().password(8, 16, true, true, true);
        } while (!isValidPassword(password));
        return password;
    }

    public boolean isValidPassword(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");
        boolean isLongEnough = password.length() >= 8;
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isLongEnough;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Book {

        String isbn;
        String title;
        String subTitle;
        String author;
        String published;
        String publisher;
        int pages;
        String description;
        String website;

        // Getters and Setters
        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class User {

        @JsonProperty("userID")
        String userID;
        String username;
        List<Book> books;


        // Getters and Setters
        public String getUserID() {
            return userID;
        }

        public void setUserId(String userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return username;
        }

        public void setUserName(String username) {
            this.username = username;
        }

        public List<Book> getBooks() {
            return books;
        }

        public void setBooks(List<Book> books) {
            this.books = books;
        }
    }

    public class AddBooksRequest {

        public String userID;
        public List<ISBN> collectionOfIsbns;

        // Constructor for adding a single book
        public AddBooksRequest(String userID, ISBN isbn) {
            this.userID = userID;
            this.collectionOfIsbns = new ArrayList<>();
            this.collectionOfIsbns.add(isbn);
        }

        // Constructor for adding multiple books
        public void SetBooksRequest(String userId, List<ISBN> isbns) {
            this.userID = userId;
            this.collectionOfIsbns = new ArrayList<>(isbns);
        }

        // Method to add a single ISBN to the collection
        public void SetBookRequest(String userId, ISBN isbn) {
            this.userID = userId;
            this.collectionOfIsbns.add(isbn);
        }

        // Method to add multiple ISBNs to the collection
        public void addISBNs(List<ISBN> isbns) {
            this.collectionOfIsbns.addAll(isbns);
        }

    }

    public class ISBN {
        public String isbn;

        public ISBN(String isbn) {
            this.isbn = isbn;
        }
    }

    @Test
    public void CreateUserWithFaker2(ITestContext context) {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification request = RestAssured.given();

        // Create an instance of Faker
        Faker faker = new Faker();

        // Generate a username and a valid password
        String username = faker.name().username();
        String password = generatePassword();

        // Storing data in a context to use for other tests
        context.setAttribute("username", username);
        context.setAttribute("password", password);

        // Create an instance of UserCredentials
        UserCredentials userCredentials = new UserCredentials(username, password);

        // Set the content type to JSON
        request.contentType("application/json");

        // Set the request body with the UserCredentials instance, Jackson is doing the serialization
        request.body(userCredentials).log().all();

        // Send the POST request and get the response
        Response response = request.post("/Account/v1/User").then().log().all()
                .extract().response();

        // Assert the status code
        assertEquals(response.statusCode(), 201);

        // Deserialize the response to User class, deserializes the response body to an instance of the User class
        User userResponse = response.getBody().as(User.class);
        //Extracting userId: The userID is extracted from the userResponse object.
        String userID = userResponse.getUserID();

        // Logging the userID
        System.out.println("userID: " + userID);

        // Storing data in a context to use for other tests
        context.setAttribute("userID", userID);
    }

    //@Test
    public void CreateUserWithFaker(ITestContext context) {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification request = RestAssured.given();

        // Create an instance of Faker
        Faker faker = new Faker();

        // Generate a username and a valid password
        String username = faker.name().username();
        String password = generatePassword();

        // Storing data in a context to use for other tests
        context.setAttribute("username", username);
        context.setAttribute("password", password);

        // Create an instance of UserCredentials
        UserCredentials userCredentials = new UserCredentials(username, password);

        // Set the content type to JSON
        request.contentType("application/json");

        // Set the request body with the UserCredentials instance, Jackson is doing the serialization
        request.body(userCredentials).log().all();

        // Send the POST request and get the response
        Response response = request.post("/Account/v1/User").then().log().all()
                .extract().response();

        // Assert the status code
        assertEquals(201, response.statusCode());

        // Extract the ISBN of the first book from the response
        String userID = response.jsonPath().getString("userID");

        // Logging the isbn
        System.out.println("userID: " + userID);

        // Storing data in a context to use for other tests
        context.setAttribute("userID", userID);
     }

    @Test(dependsOnMethods = "CreateUserWithFaker2")
    public void GetBooks(ITestContext context) {
        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        RequestSpecification request = RestAssured.given();

        // Set the content type to JSON
        request.contentType("application/json");

        // Send the POST request and get the response
        Response response = request.get("/BookStore/v1/Books").then().log().all()
                .extract().response();

        // Assert the status code
        assertEquals(response.statusCode(),200);

        // Deserialize the response to a list of Book objects
        List<Book> allBooks = response.jsonPath().getList("books", Book.class);

        // Extract the ISBN of the first book from the response
        String isbnToAdd = allBooks.get(0).getIsbn();

        // Logging the isbn
        System.out.println("isbnToAdd: " + isbnToAdd);

        // Storing data in a context to use for other tests
        context.setAttribute("isbnToAdd", isbnToAdd);

        // Iterate over the list and print individual book item
        // Note that every book entry in the list will be complete Json object of book
        for(Book book : allBooks) {
            System.out.println("Book: " + book.getTitle());
        }

        // We can convert the Json Response directly into a Java Array by using
        // JsonPath.getObject method. Here we have to specify that we want to
        // deserialize the Json into an Array of Book. This can be done by specifying
        // Book[].class as the second argument to the getObject method.
        Book[] books = response.jsonPath().getObject("books",Book[].class );

        for(Book book : books)
        {
            System.out.println("Book title " + book.title);
        }

        // Extract the ISBN of the first book from the response
        //String isbnToAdd = response.jsonPath().getString("books[0].isbn");
        // Read all the books as a List of String. Each item in the list
        // represent a book node in the REST service Response
        //List<String> allBooks = response.jsonPath().getList("books.title");
        //
        // Iterate over the list and print individual book item
        //for(String book : allBooks)
        //{
        //    System.out.println("Book: " + book);
        //}
    }

    //@Test (dependsOnMethods = "GetBooks")
    public void PostBook2(ITestContext context) {

        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        // Retrieve username and password from context
        String username = (String) context.getAttribute("username");
        String password = (String) context.getAttribute("password");
        String isbnToAdd = (String) context.getAttribute("isbnToAdd");
        String userID = (String) context.getAttribute("userID");


        // Create the basic auth credentials
        String credentials = username + ":" + password;
        byte[] encodedCredentials = Base64.encodeBase64(credentials.getBytes());
        String encodedCredentialsAsString = new String(encodedCredentials);
        AddBooksRequest addBooksRequest = new AddBooksRequest(userID, new ISBN(isbnToAdd));

        Response response = given()
                .header("Authorization","Basic "+encodedCredentialsAsString)
                .header("Content-Type","application/json")
                .body(addBooksRequest)
                .log().all().when().post("/BookStore/v1/Books").then().log().all()
                .extract().response();

        assertEquals(201, response.statusCode());


    }


    //@Test(dependsOnMethods = "GetBooks")
    public void PostBook12(ITestContext context) {

        RestAssured.baseURI = "https://bookstore.toolsqa.com";
        // Retrieve username and password from context
        String username = (String) context.getAttribute("username");
        String password = (String) context.getAttribute("password");
        String userID = (String) context.getAttribute("userID");
        String isbn = (String) context.getAttribute("isbnToAdd");



        // Create the basic auth credentials
        String credentials = username + ":" + password;
        byte[] encodedCredentials = Base64.encodeBase64(credentials.getBytes());
        String encodedCredentialsAsString = new String(encodedCredentials);

        ISBN newIsbn = new ISBN(isbn);
        AddBooksRequest newBook = new AddBooksRequest(userID,newIsbn);
        String payload = "{\r\n" +
                "  \"userId\": \""+userID+"\",\r\n" +
                "  \"collectionOfIsbns\": [\r\n" +
                "    {\r\n" +
                "      \"isbn\": \"" + newIsbn + "\"\r\n" +
                "    }\r\n" +
                "  ]\r\n" +
                "}";


        Response response = given()
                .header("Authorization","Basic "+encodedCredentialsAsString)
                .header("Content-Type","application/json")
                .body(newBook)
                .log().all().when().post("/BookStore/v1/Books").then().log().all()
                .extract().response();

        assertEquals(201, response.statusCode());


    }





}
