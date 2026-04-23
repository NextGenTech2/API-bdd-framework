package com.example.api.models;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class RequestBuilder {
    private RequestSpecBuilder builder;

    public RequestBuilder() {
        this.builder = new RequestSpecBuilder();
    }
    public RequestBuilder addBody(Object body) {
    if (body != null) {
        this.builder.setBody(body);
    }
    return this;
}

    public RequestBuilder addPathParams(Map<String, String> pathParams) {
        // CRITICAL: Prevent the "cannot be null" crash
        if (pathParams != null && !pathParams.isEmpty()) {
            this.builder.addPathParams(pathParams);
        }
        return this;
    }

    public RequestBuilder addQueryParams(Map<String, String> queryParams) {
        if (queryParams != null && !queryParams.isEmpty()) {
            this.builder.addQueryParams(queryParams);
        }
        return this;
    }

    public RequestBuilder addHeaders(Map<String, String> headers) {
        if (headers != null) {
            this.builder.addHeaders(headers);
        }
        return this;
    }

    public RequestSpecification build() {
        return this.builder.build();
    }
}
