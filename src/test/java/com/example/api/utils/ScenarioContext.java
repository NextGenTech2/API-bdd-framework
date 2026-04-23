package com.example.api.utils;

import com.example.api.models.ApiResponse;

import java.util.HashMap; 
import java.util.Map;

public class ScenarioContext { 
    
    private static final ThreadLocal<Map<String, Object>> context = ThreadLocal.withInitial(HashMap::new);

public static void setCurrentEndpoint(String endpoint) {
    context.get().put("currentEndpoint", endpoint);
}

public static String getCurrentEndpoint() {
    return (String) context.get().get("currentEndpoint");
}

public static void setPathParameters(Map<String, String> pathParams) {
    context.get().put("pathParameters", pathParams);
}

public static Map<String, String> getPathParameters() {
    //noinspection unchecked
    return (Map<String, String>) context.get().get("pathParameters");
}

public static void setQueryParameters(Map<String, String> queryParams) {
    context.get().put("queryParameters", queryParams);
}

public static Map<String, String> getQueryParameters() {
    //noinspection unchecked
    return (Map<String, String>) context.get().get("queryParameters");
}

public static void setRequestBody(Object requestBody) {
    context.get().put("requestBody", requestBody);
}

public static Object getRequestBody() {
    return context.get().get("requestBody");
}

public static void setCurrentResponse(ApiResponse response) {
    context.get().put("currentResponse", response);
}

public static ApiResponse getCurrentResponse() {
    return (ApiResponse) context.get().get("currentResponse");
}

}