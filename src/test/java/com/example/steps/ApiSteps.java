package com.example.steps;

import com.example.api.models.ApiResponse;
import com.example.api.services.ApiService;
import com.example.api.utils.ResponseValidator;
import com.example.api.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ApiSteps {

    private String savedAuthToken;
    private Map<String, String> customHeaders = new java.util.HashMap<>();

    @Given("the API endpoint is {string}")
    public void theAPIEndpointIs(String endpoint) {
        ScenarioContext.setCurrentEndpoint(endpoint);
    }

    @And("the path parameters are:")
    public void thePathParametersAre(Map<String, String> pathParams) {
        ScenarioContext.setPathParameters(pathParams);
    }

    @And("the query parameters are:")
    public void theQueryParametersAre(Map<String, String> queryParams) {
        ScenarioContext.setQueryParameters(queryParams);
    }

    @And("the request body is:")
    public void theRequestBodyIs(String requestBody) {
        ScenarioContext.setRequestBody(requestBody);
    }

    @When("the client sends a GET request")
   public void theClientSendsAGETRequest() {
        
        ApiResponse response;
        if (customHeaders != null && !customHeaders.isEmpty()) {
            response = ApiService.makeGetRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getQueryParameters(),
                    customHeaders);
        } else {
            response = ApiService.makeGetRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getQueryParameters());
        }
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a POST request")
    public void theClientSendsAPOSTRequest() {
        ApiResponse response = ApiService.makePostRequest(
                ScenarioContext.getCurrentEndpoint(),
                ScenarioContext.getPathParameters(),
                ScenarioContext.getRequestBody());
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a PUT request")
    public void theClientSendsAPUTRequest() {
        ApiResponse response = ApiService.makePutRequest(
                ScenarioContext.getCurrentEndpoint(),
                ScenarioContext.getPathParameters(),
                ScenarioContext.getRequestBody());
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a DELETE request")
    public void theClientSendsADELETERequest() {
        ApiResponse response = ApiService.makeDeleteRequest(
                ScenarioContext.getCurrentEndpoint(),
                ScenarioContext.getPathParameters());
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a PATCH request")
    public void theClientSendsAPATCHRequest() {
        ApiResponse response = ApiService.makePatchRequest(
                ScenarioContext.getCurrentEndpoint(),
                ScenarioContext.getPathParameters(),
                ScenarioContext.getRequestBody());
        ScenarioContext.setCurrentResponse(response);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        ResponseValidator.validateStatusCode(ScenarioContext.getCurrentResponse(), expectedStatusCode);
    }

    @And("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedResponseBody) {
        ResponseValidator.validateResponseBody(ScenarioContext.getCurrentResponse(), expectedResponseBody);
    }

    @And("the request body is from the file {string} with the key {string}")
public void theRequestBodyFromTheFile(String filePath, String key) {
    try {
        // Use the Thread context class loader - most compatible for Cucumber runners
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        // Try to load the stream
        InputStream is = classLoader.getResourceAsStream(filePath);
        
        // Fallback: Some environments require a leading slash to find resources
        if (is == null && !filePath.startsWith("/")) {
            is = classLoader.getResourceAsStream("/" + filePath);
        }

        if (is == null) {
            throw new RuntimeException("Resource not found on classpath: " + filePath + 
                ". Check that the file is in src/test/resources and path is correct.");
        }

        // Read bytes and convert to String using UTF-8 for cross-platform safety
        String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        
        // Parse JSON
        JSONObject allPayloads = new JSONObject(jsonContent);
        
        if (!allPayloads.has(key)) {
            throw new RuntimeException("Key '" + key + "' not found in JSON file. Available keys: " + allPayloads.keySet());
        }

        String finalPayload = allPayloads.getJSONObject(key).toString();
        
        // Set your context
        ScenarioContext.setRequestBody(finalPayload);
        
        // Close stream
        is.close();

    } catch (IOException e) {
        throw new RuntimeException("IO Error reading JSON file: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("General Error processing JSON: " + e.getMessage());
    }
}
    

    @And("the response body should match the following details:")
    public void theResponseBodyShouldMatch(io.cucumber.datatable.DataTable dataTable) {
        // 1. Convert the Gherkin table into a List of Maps
        List<Map<String, String>> expectedData = dataTable.asMaps(String.class, String.class);
        Map<String, String> expectedRow = expectedData.get(0);

        // 2. Get the actual response body
        String responseBody = ScenarioContext.getCurrentResponse().getBody();
        io.restassured.path.json.JsonPath jsonPath = io.restassured.path.json.JsonPath.from(responseBody);

        // 3. Iterate through each header in the table and validate
        expectedRow.forEach((key, expectedValue) -> {
            Object actualValue;

            // --- SMART PATH LOGIC ---
            // If the JSON starts with '[', it's a list; use index [0].
            // If it starts with '{', it's an object (like ReqRes); use key directly.
            if (responseBody.trim().startsWith("[")) {
                actualValue = jsonPath.get("[0]." + key);
            } else {
                actualValue = jsonPath.get(key);
            }

            // 4. Validation with descriptive error message
            Assert.assertNotNull("Field [" + key + "] not found in response! \nFull Response: " + responseBody,
                    actualValue);

            Assert.assertEquals(
                    "\n DATA MISMATCH!" +
                            "\n Field:    " + key +
                            "\n Expected: " + expectedValue +
                            "\n Actual:   " + actualValue +
                            "\n Full JSON: " + responseBody,
                    expectedValue.toLowerCase(), // Use toLowerCase to handle 'janet' vs 'Janet'
                    String.valueOf(actualValue).toLowerCase());
        });
    }

    @Then("the response should contain the text {string}")
    public void theResponseShouldContainTheText(String expectedText) {
        String responseBody = ScenarioContext.getCurrentResponse().getBody();
        Assert.assertTrue("Expected response to contain text: '" + expectedText + "', but actual response body was: " + responseBody,
                responseBody.contains(expectedText));
    }
    
    @And("the response array should contain the following items:")
    public void theResponseArrayShouldContainTheFollowingItems(io.cucumber.datatable.DataTable dataTable) {
        String responseBody = ScenarioContext.getCurrentResponse().getBody();
        JSONArray jsonArray = new JSONArray(responseBody);
        List<String> expectedItems = dataTable.asList();

        for (String expectedItem : expectedItems) {
            boolean found = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(expectedItem)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("Expected item '" + expectedItem + "' was not found in the response array.", found);
        }
    }

    @And("the client extracts the {string} from the response as a Bearer token")
    public void theClientExtractsTheFromTheResponseAsABearerToken(String jsonKey) {
        String responseBody = ScenarioContext.getCurrentResponse().getBody();
        savedAuthToken = io.restassured.path.json.JsonPath.from(responseBody).getString(jsonKey);
        Assert.assertNotNull("Extracted token should not be null", savedAuthToken);
    }

    @And("the client sets the Bearer token in the header")
    public void theClientSetsTheBearerTokenInTheHeader() {
        Assert.assertNotNull("No token was previously saved! Ensure you extracted it first.", savedAuthToken);
        customHeaders.put("Authorization", "Bearer " + savedAuthToken);
    }

    @And("the client sets an invalid Bearer token in the header")
    public void theClientSetsAnInvalidBearerTokenInTheHeader() {
        // Using a structurally valid JWT format to prevent WAFs/Servers from dropping the connection
        // The payload contains {"id":1,"username":"emilys","exp":1516239022} to prevent 500 errors from missing fields
        customHeaders.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJlbWlseXMiLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    }

    

}
