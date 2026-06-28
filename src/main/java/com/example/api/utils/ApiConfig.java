package com.example.api.utils;

import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Configuration for the API Framework.
 * As an Engineering Manager, you might eventually move BASE_URL to 
 * a properties file, but this hardcoded approach is perfect for debugging now.
 */
public class ApiConfig { 
    
    private static String BASE_URL;
    private static final Map<String, String> HEADERS = new HashMap<>();

    static {
        // 1. Load default properties from the config.properties file
        java.util.Properties prop = new java.util.Properties();
        try (java.io.InputStream input = ApiConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                prop.load(input);
                BASE_URL = prop.getProperty("base.url");
            }
        } catch (Exception e) {
            // Fallback if file not found or error loading
        }

        // 2. Check if a System Property was passed via command line (-Dbase.url=...)
        String systemUrl = System.getProperty("base.url");
        if (systemUrl != null && !systemUrl.isEmpty()) {
            BASE_URL = systemUrl;
        }

        // 3. Absolute fallback if neither is configured
        if (BASE_URL == null || BASE_URL.isEmpty()) {
            BASE_URL = "https://practice.expandtesting.com"; 
        }

        // Set the Global Base URI so RestAssured prepends it automatically
        RestAssured.baseURI = BASE_URL;

        // Initialize Default Headers
        HEADERS.put("Content-Type", "application/json");
        HEADERS.put("Accept", "application/json");
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static Map<String, String> getHeaders() {
        return new HashMap<>(HEADERS);
    }
}