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

    private static void logRequestDetails(String method, String url, Map<String, String> pathParams, Map<String, String> queryParams, Map<String, String> headers, Object body) {
        System.out.println("\n========== API REQUEST ==========");
        System.out.println("Method       : " + method);
        System.out.println("URL          : " + url);
        if (pathParams != null && !pathParams.isEmpty()) System.out.println("Path Params  : " + pathParams);
        if (queryParams != null && !queryParams.isEmpty()) System.out.println("Query Params : " + queryParams);
        if (headers != null && !headers.isEmpty()) System.out.println("Headers      : " + headers);
        if (body != null) System.out.println("Body         : " + body);
        System.out.println("=================================\n");
    }

    public static ApiResponse makeGetRequest(String endpoint, Map<String, String> pathParams, Map<String, String> queryParams, Map<String, String> customHeaders) { 
    String fullUrl = ApiConfig.getBaseUrl() + endpoint; 

    // Combine default configuration headers with our dynamic custom headers
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }

    logRequestDetails("GET", fullUrl, pathParams, queryParams, finalHeaders, null);

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

    logRequestDetails("GET", fullUrl, pathParams, queryParams, ApiConfig.getHeaders(), null);

    RequestSpecification requestSpec = new RequestBuilder()
        .addPathParams(pathParams)
        .addQueryParams(queryParams)
        .addHeaders(ApiConfig.getHeaders())
        .build();

    Response response = RestClient.sendGetRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePostRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    
    logRequestDetails("POST", fullUrl, pathParams, null, ApiConfig.getHeaders(), requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    Response response = RestClient.sendPostRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePostRequest(String endpoint, Map<String, String> pathParams, Object requestBody, Map<String, String> customHeaders) {
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }
    
    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("POST", fullUrl, pathParams, null, finalHeaders, requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(finalHeaders)
            .build();

    Response response = RestClient.sendPostRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePutRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("PUT", fullUrl, pathParams, null, ApiConfig.getHeaders(), requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    Response response = RestClient.sendPutRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePutRequest(String endpoint, Map<String, String> pathParams, Object requestBody, Map<String, String> customHeaders) {
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("PUT", fullUrl, pathParams, null, finalHeaders, requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(finalHeaders)
            .build();

    Response response = RestClient.sendPutRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePatchRequest(String endpoint, Map<String, String> pathParams, Object requestBody) {
    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("PATCH", fullUrl, pathParams, null, ApiConfig.getHeaders(), requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    Response response = RestClient.sendPatchRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makePatchRequest(String endpoint, Map<String, String> pathParams, Object requestBody, Map<String, String> customHeaders) {
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("PATCH", fullUrl, pathParams, null, finalHeaders, requestBody);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addBody(requestBody)
            .addHeaders(finalHeaders)
            .build();

    Response response = RestClient.sendPatchRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makeDeleteRequest(String endpoint, Map<String, String> pathParams) {
    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("DELETE", fullUrl, pathParams, null, ApiConfig.getHeaders(), null);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addHeaders(ApiConfig.getHeaders())
            .build();

    Response response = RestClient.sendDeleteRequest(fullUrl, requestSpec);
    return new ApiResponse(response);
}

public static ApiResponse makeDeleteRequest(String endpoint, Map<String, String> pathParams, Map<String, String> customHeaders) {
    Map<String, String> finalHeaders = new java.util.HashMap<>(ApiConfig.getHeaders());
    if (customHeaders != null) {
        finalHeaders.putAll(customHeaders);
    }

    String fullUrl = ApiConfig.getBaseUrl() + endpoint;
    logRequestDetails("DELETE", fullUrl, pathParams, null, finalHeaders, null);

    RequestSpecification requestSpec = new RequestBuilder()
            .addPathParams(pathParams)
            .addHeaders(finalHeaders)
            .build();

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