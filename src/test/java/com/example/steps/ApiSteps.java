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
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiSteps {

    private String savedAuthToken;
    private Map<String, String> customHeaders = new java.util.HashMap<>();

    @Given("the API endpoint is {string}")
    public void theAPIEndpointIs(String endpoint) {
        ScenarioContext.setCurrentEndpoint(endpoint);
    }

    @And("the path parameters are:")
    public void thePathParametersAre(Map<String, String> pathParams) {
        // Process path parameters to support {{placeholders}} saved in the ScenarioContext
        Map<String, String> processedParams = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            processedParams.put(entry.getKey(), processPayload(entry.getValue()));
        }
        ScenarioContext.setPathParameters(processedParams);
    }

    @And("the query parameters are:")
    public void theQueryParametersAre(Map<String, String> queryParams) {
        ScenarioContext.setQueryParameters(queryParams);
    }

    @And("the request body is:")
    public void theRequestBodyIs(String requestBody) {
        ScenarioContext.setRequestBody(requestBody);
    }

    @And("the request body is formed from the following details:")
    public void theRequestBodyIsFormedFromDetails(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMaps().get(0);
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String value = entry.getValue();
            // Smart type conversion for JSON
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                jsonObject.put(entry.getKey(), Boolean.parseBoolean(value));
            } else if (value != null && value.matches("-?\\d+")) {
                jsonObject.put(entry.getKey(), Integer.parseInt(value));
            } else {
                jsonObject.put(entry.getKey(), value);
            }
        }
        ScenarioContext.setRequestBody(jsonObject.toString());
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
        ApiResponse response;
        if (customHeaders != null && !customHeaders.isEmpty()) {
            response = ApiService.makePostRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody(),
                    customHeaders);
        } else {
            response = ApiService.makePostRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody());
        }
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a PUT request")
    public void theClientSendsAPUTRequest() {
        ApiResponse response;
        if (customHeaders != null && !customHeaders.isEmpty()) {
            response = ApiService.makePutRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody(),
                    customHeaders);
        } else {
            response = ApiService.makePutRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody());
        }
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a DELETE request")
    public void theClientSendsADELETERequest() {
        ApiResponse response;
        if (customHeaders != null && !customHeaders.isEmpty()) {
            response = ApiService.makeDeleteRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    customHeaders);
        } else {
            response = ApiService.makeDeleteRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters());
        }
        ScenarioContext.setCurrentResponse(response);
    }

    @When("the client sends a PATCH request")
    public void theClientSendsAPATCHRequest() {
        ApiResponse response;
        if (customHeaders != null && !customHeaders.isEmpty()) {
            response = ApiService.makePatchRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody(),
                    customHeaders);
        } else {
            response = ApiService.makePatchRequest(
                    ScenarioContext.getCurrentEndpoint(),
                    ScenarioContext.getPathParameters(),
                    ScenarioContext.getRequestBody());
        }
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

        String rawPayload = allPayloads.getJSONObject(key).toString();
        
        // Process the payload for any dynamic values like {{random.email}}
        String processedPayload = processPayload(rawPayload);
        ScenarioContext.setRequestBody(processedPayload);
        
        // Close stream
        is.close();

    } catch (IOException e) {
        throw new RuntimeException("IO Error reading JSON file: " + e.getMessage());
    } catch (Exception e) {
        throw new RuntimeException("General Error processing JSON: " + e.getMessage());
    }
}
    
    @And("the request body is from the file {string} with the key {string} and parameterized with:")
    public void theRequestBodyFromFileWithKeyAndParameterized(String filePath, String key, io.cucumber.datatable.DataTable dataTable) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream is = classLoader.getResourceAsStream(filePath);
            
            if (is == null && !filePath.startsWith("/")) {
                is = classLoader.getResourceAsStream("/" + filePath);
            }

            if (is == null) {
                throw new RuntimeException("Resource not found on classpath: " + filePath + 
                    ". Check that the file is in src/test/resources and path is correct.");
            }

            String jsonContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject allPayloads = new JSONObject(jsonContent);
            
            if (!allPayloads.has(key)) {
                throw new RuntimeException("Key '" + key + "' not found in JSON file. Available keys: " + allPayloads.keySet());
            }

            JSONObject basePayload = allPayloads.getJSONObject(key);
            
            // Parameterize from DataTable
            Map<String, String> data = dataTable.asMaps().get(0);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String value = entry.getValue();
                // Smart type conversion for JSON
                if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                    basePayload.put(entry.getKey(), Boolean.parseBoolean(value));
                } else if (value != null && value.matches("-?\\d+")) {
                    basePayload.put(entry.getKey(), Integer.parseInt(value));
                } else {
                    basePayload.put(entry.getKey(), value);
                }
            }

            String processedPayload = processPayload(basePayload.toString());
            ScenarioContext.setRequestBody(processedPayload);
            
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Error processing parameterized JSON: " + e.getMessage());
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
            String finalExpectedValue = expectedValue; // Start with the original value

            // Check if the expected value is a dynamic placeholder like {{random.email}}
            if (expectedValue != null && expectedValue.matches("\\{\\{([^}]+)\\}\\}")) {
                Object contextValue = ScenarioContext.getData(expectedValue);

                // Fallback: check if the value was saved without curly braces (e.g., 'noteId' instead of '{{noteId}}')
                if (contextValue == null) {
                    String instruction = expectedValue.substring(2, expectedValue.length() - 2);
                    contextValue = ScenarioContext.getData(instruction);
                }

                if (contextValue != null) {
                    finalExpectedValue = String.valueOf(contextValue);
                } else {
                    // If placeholder not found in context, it's a test setup error.
                    Assert.fail("Placeholder '" + expectedValue + "' was used in validation table but not found in ScenarioContext. " +
                                "Ensure it was generated in the request payload.");
                }
            }

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
                            "\n Expected: " + finalExpectedValue +
                            "\n Actual:   " + actualValue +
                            "\n Full JSON: " + responseBody,
                    finalExpectedValue.toLowerCase(), // Use toLowerCase to handle 'janet' vs 'Janet'
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
    
    @And("the client extracts the {string} from the response and saves it as {string}")
    public void theClientExtractsTheFromResponseAndSavesAs(String jsonPath, String variableName) {
        String responseBody = ScenarioContext.getCurrentResponse().getBody();
        Object extractedValue = io.restassured.path.json.JsonPath.from(responseBody).get(jsonPath);
        Assert.assertNotNull("Value at path '" + jsonPath + "' was null in the response.", extractedValue);
        ScenarioContext.setData(variableName, extractedValue);
    }

    @And("the client sets the Bearer token in the header")
    public void theClientSetsTheBearerTokenInTheHeader() {
        Assert.assertNotNull("No token was previously saved! Ensure you extracted it first.", savedAuthToken);
        customHeaders.put("Authorization", "Bearer " + savedAuthToken);
    }

    @And("the client sets the extracted token in the {string} header")
    public void theClientSetsTheExtractedTokenInTheHeader(String headerName) {
        Assert.assertNotNull("No token was previously saved! Ensure you extracted it first.", savedAuthToken);
        customHeaders.put(headerName, savedAuthToken);
    }

    @And("the client sets an invalid token in the {string} header")
    public void theClientSetsAnInvalidTokenInTheHeader(String headerName) {
        customHeaders.put(headerName, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJlbWlseXMiLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    }

    @And("the client sets an invalid Bearer token in the header")
    public void theClientSetsAnInvalidBearerTokenInTheHeader() {
        // Using a structurally valid JWT format to prevent WAFs/Servers from dropping the connection
        // The payload contains {"id":1,"username":"emilys","exp":1516239022} to prevent 500 errors from missing fields
        customHeaders.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJlbWlseXMiLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
    }

    // --- DYNAMIC DATA HELPERS ---

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * Processes a payload string, finds placeholders like {{random.email}},
     * replaces them with generated data, and stores the generated data in ScenarioContext.
     */
    private static String processPayload(String payload) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(payload);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(0); // e.g., {{random.email}}
            String instruction = matcher.group(1); // e.g., random.email
            String generatedValue;

            // Check if we've already generated a value for this placeholder in this scenario
            if (ScenarioContext.getData(placeholder) != null) {
                // Fixed potential ClassCastException by using String.valueOf() to support numbers, booleans etc
                generatedValue = String.valueOf(ScenarioContext.getData(placeholder));
            } else if (ScenarioContext.getData(instruction) != null) {
                // Check if the data was stored using the instruction name (e.g., 'noteId' instead of '{{noteId}}')
                generatedValue = String.valueOf(ScenarioContext.getData(instruction));
            } else {
                // Generate a new value based on the instruction
                switch (instruction.toLowerCase()) {
                    case "random.email":
                        generatedValue = generateRandomEmail();
                        break;
                    case "random.name":
                        generatedValue = generateRandomName();
                        break;
                    default:
                        // If instruction is unknown, leave the placeholder as is
                        generatedValue = placeholder;
                }
                // Store the newly generated value in the context for validation later
                if (!generatedValue.equals(placeholder)) {
                    ScenarioContext.setData(placeholder, generatedValue);
                }
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(generatedValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String generateRandomEmail() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "testuser_" + uuid + "@expandtesting.com";
    }

    private static String generateRandomName() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("Random user is " + "TestUser_" + uuid);
        return "TestUser_" + uuid;
        
    }

}
