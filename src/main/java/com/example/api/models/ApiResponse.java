package com.example.api.models;

import io.restassured.response.Response;

public class ApiResponse {

private int statusCode; private String body;

public ApiResponse(Response response) {
    this.statusCode = response.getStatusCode();
    this.body = response.getBody().asString();
}

public int getStatusCode() {
    return statusCode;
}

public String getBody() {
    return body;
}
}