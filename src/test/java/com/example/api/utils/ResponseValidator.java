package com.example.api.utils;

import com.example.api.models.ApiResponse;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;

public class ResponseValidator { 

public static void validateStatusCode(ApiResponse response, int expectedStatusCode) { 
    Assert.assertEquals(expectedStatusCode, response.getStatusCode()); 
    
}
public static void validateResponseBody(ApiResponse response, String expectedBody) {
    //Assert.assertEquals(expectedBody, response.getBody());
   Assert.assertTrue("Expected body to contain: " + expectedBody + " but got: " + response.getBody(),
                response.getBody().contains(expectedBody));
    
}
}