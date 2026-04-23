package com.example.api.services;

import com.example.api.client.RestClient; 
import com.example.api.models.ApiResponse; 
import com.example.api.models.RequestBuilder; 
import com.example.api.utils.ApiConfig; 
import com.example.api.utils.AuthManager; 
import io.restassured.response.Response; 
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class ApiService { 

    public static ApiResponse makeGetRequest(String endpoint, Map<String, String> pathParams, Map<String, String> queryParams, Map<String, String> customHeaders) { 
    String fullUrl = ApiConfig.getBaseUrl() + endpoint; 

    // Combine default configuration headers with our dynamic custom headers
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }

    RequestSpecification requestSpec = new RequestBuilder()
        .addPathParams(pathParams)
        .addQueryParams(queryParams)
        .addHeaders(finalHeaders)
        .build();

    Response response = RestClient.sendGetRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makeGetRequest(String endpoint, Map<String, String> pathParams, Map<String, String> queryParams) { 
    
    String fullUrl = ApiConfig.getBaseUrl() + endpoint; 

    RequestSpecification requestSpec = new RequestBuilder()
        .addPathParams(pathParams)
        .addQueryParams(queryParams)
        .addHeaders(ApiConfig.getHeaders())
        .build();

    Response response = RestClient.sendGetRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePostRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    Response response = RestClient.sendPostRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePutRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    Response response = RestClient.sendPutRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePatchRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    Response response = RestClient.sendPatchRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makeDeleteRequest(String endpoint, Map<String, String> pathParams) {
    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    Response response = RestClient.sendDeleteRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static void login(String username, String password) {
    // Implement login logic using AuthManager
}

public static void logout() {
    // Implement logout logic using AuthManager
}
}