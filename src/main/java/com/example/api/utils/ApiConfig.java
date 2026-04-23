package com.example.api.utils;

import io.restassured.RestAssured;
import io.restassured.http.Header;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Configuration for the API Framework.
 * As an Engineering Manager, you might eventually move BASE_URL to 
 * a properties file, but this hardcoded approach is perfect for debugging now.
 */
public class ApiConfig { 
    
    //private static final String BASE_URL = "https://jsonplaceholder.typicode.com"; 
    // private static final String BASE_URL = "https://jsonplaceholder.cypress.io"; 
     //private static final String BASE_URL = "https://reqres.in/api";
     //private static final String BASE_URL = "https://restful-booker.herokuapp.com";
     //private static final String API_KEY = "pro_1719467d04af4bfbe009845d4a9e91885a53dbed7dc5cb8fdc5350f32aedd85c"; 
    private static final String BASE_URL = "https://dummyjson.com";

    private static final Map<String, String> HEADERS = new HashMap<>();

    static {
        // 1. Set the Global Base URI so RestAssured prepends it automatically
        RestAssured.baseURI = BASE_URL;

        // 2. Initialize Default Headers
        HEADERS.put("Content-Type", "application/json");
        HEADERS.put("Accept", "application/json");
        // Login and get the API key https://app.reqres.in/ below header is only required when trying with https://reqres.in/api" as BASE URL 
        //HEADERS.put("x-api-key", API_KEY);
        // Replace <token> with your actual Bearer token if required
        //HEADERS.put("Authorization", "Bearer your_actual_token_here");
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static Map<String, String> getHeaders() {
        // Return a copy or the original map for the RequestBuilder
        return new HashMap<>(HEADERS);
    }
}