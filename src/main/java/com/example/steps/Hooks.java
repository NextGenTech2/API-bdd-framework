package com.example.steps;

import com.example.api.utils.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class Hooks {
    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            String responseBody = ScenarioContext.getCurrentResponse().getBody();
            // This attaches the response body directly into the Masterthought HTML report
            scenario.log("API Response Body on Failure: " + responseBody);
        }
    }
}