package com.example.api.client;
import io.restassured.RestAssured; 
import io.restassured.response.Response; 
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class RestClient {
public static Response sendGetRequest(String endpoint, RequestSpecification requestSpec) {
    return given()
            .spec(requestSpec)
            .log().ifValidationFails()
            .when()
            .get(endpoint)
            .then()
            .extract()
            .response();
}

public static Response sendPostRequest(String endpoint, RequestSpecification requestSpec) {
    return given()
            .spec(requestSpec)
            .when()
            .post(endpoint)
            .then()
            .extract()
            .response();
}

public static Response sendPutRequest(String endpoint, RequestSpecification requestSpec) {
    return given()
            .spec(requestSpec)
            .when()
            .put(endpoint)
            .then()
            .extract()
            .response();
}

public static Response sendPatchRequest(String endpoint, RequestSpecification requestSpec) {
    return given()
            .spec(requestSpec)
            .when()
            .patch(endpoint)
            .then()
            .extract()
            .response();
}

public static Response sendDeleteRequest(String endpoint, RequestSpecification requestSpec) {
    return given()
            .spec(requestSpec)
            .when()
            .delete(endpoint)
            .then()
            .extract()
            .response();
}
}