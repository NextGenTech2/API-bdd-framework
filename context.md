# API Automation Framework Documentation

Welcome to the API Automation team! This document serves as a guide to understand the architecture, standards, and best practices for our API testing framework.

## 🛠️ Tech Stack
*   **Language**: Java
*   **BDD Framework**: Cucumber
*   **HTTP Client**: RestAssured
*   **Test Runner**: JUnit 4
*   **JSON Processing**: `org.json` & `JsonPath`

---

## 📂 Project Structure

Our framework is cleanly separated into main framework code and test execution code:

```text
API-AUTOMATION/
├── src/main/java/com/example/api/
│   ├── client/       # RestClient.java (Wrapper around RestAssured methods: GET, POST, etc.)
│   ├── models/       # POJOs for Requests and Responses (ApiResponse, RequestBuilder)
│   ├── services/     # ApiService.java (Prepares request specs, URLs, and executes via RestClient)
│   └── utils/        # Core utilities: ApiConfig, AuthManager, ResponseValidator, ScenarioContext
├── src/test/java/com/example/
│   ├── runners/      # ApiRunnerTest.java (JUnit runner configuration & tags)
│   └── steps/        # Hooks.java & ApiSteps.java (Cucumber Step Definitions)
├── src/test/resources/
│   ├── features/     # Cucumber .feature files (Gherkin scenarios)
│   └── payloads/     # payloads.json (Centralized test data / JSON bodies)
```

---

## 🏗️ Core Architecture Patterns

### 1. State Management (`ScenarioContext.java`)
Cucumber creates a new instance of step definition classes for every scenario. To safely share data between `@Given`, `@When`, and `@Then` steps without using static variables (which break parallel execution), we use **`ScenarioContext`**. It internally utilizes `ThreadLocal` to store context data for the current executing thread safely.
*   *Examples*: Saving an endpoint, an extracted auth token from a login response, or a newly created resource ID (`noteId`) to be used in subsequent steps.

### 2. Payload Management (`payloads.json`)
To keep our `.feature` files readable, we strictly **do not** write inline JSON strings. Instead, all request payloads are stored in `src/test/resources/payloads/payloads.json` as key-value pairs.
Our framework also supports two powerful dynamic data features:
*   **Dynamic Value Generation**: Placeholders like `{{random.email}}` or `{{random.name}}` in your `payloads.json` file will be automatically replaced with unique generated data for each test run.
*   **Payload Parameterization**: You can override values in a template payload directly from your feature file using a `DataTable`, which is perfect for data-driven testing.

### 3. Assertions (`ResponseValidator.java`)
All assertions regarding status codes and response bodies are centralized in the `ResponseValidator` class to maintain consistency and provide unified error messages across all tests.

---

## 🚀 How to Automate an End-to-End Scenario

When adding new tests, follow these steps to maintain standards:

### Step 1: Add Test Data to `payloads.json`
If your API request requires a body (POST/PUT/PATCH), open `payloads.json` and add a new JSON object with a descriptive key. You can use placeholders for dynamic data.
```json
"user_registration": {
  "name": "{{random.name}}",
  "email": "{{random.email}}",
  "password": "Tester123"
},
"note_template": {
  "title": "{{title}}",
  "description": "{{description}}",
  "category": "{{category}}"
}
```

### Step 2: Write the Feature File
Create or update a `.feature` file in `src/test/resources/features/`. Use our highly reusable predefined step definitions so you rarely need to write new Java code.

```gherkin
@users @Positive
Scenario: Successfully create a new user
  Given the API endpoint is "/users"
  And the request body is from the file "payloads/payloads.json" with the key "create_user_success"
  When the client sends a POST request
  Then the response status code should be 201
  And the response body should match the following details:
    | name     | job               |
    | Jane Doe | Software Engineer |
```

### Step 3: Run and Verify
Run the scenario using your IDE or via Maven/Gradle using the tag (e.g., `@users`). If it passes, a detailed HTML report will be generated in `target/cucumber-reports/`.

---

## 📖 Standard Step Definitions Reference

Here is a cheat sheet of the available steps mapped in `ApiSteps.java`:

### Preparation Steps (`@Given`, `@And`)
*   `Given the API endpoint is "{endpoint}"`
*   `And the request body is from the file "{filePath}" with the key "{key}"`
*   `And the path parameters are:` (Accepts a DataTable)
*   `And the query parameters are:` (Accepts a DataTable)

### Execution Steps (`@When`)
*   `When the client sends a GET request`
*   `When the client sends a POST request`
*   `When the client sends a PUT request`
*   `When the client sends a PATCH request`
*   `When the client sends a DELETE request`

### Validation Steps (`@Then`, `@And`)
*   `Then the response status code should be {int}`
*   `And the response body should contain "{text}"`
*   `And the response body should match the following details:`
    *(Accepts a DataTable. Smartly matches keys for JSON Objects `[0]` vs Lists)*
*   `And the response array should contain the following items:`
    *(Accepts a DataTable list of elements to search for in a JSON Array)*

### Authentication & Chaining Steps
*   `And the client extracts the "{jsonKey}" from the response as a Bearer token`
    *(Saves the extracted token in memory for the current scenario)*
*   `And the client sets the Bearer token in the header`
    *(Injects the previously saved token into the next request)*
*   `And the client sets an invalid Bearer token in the header`

---

## 🛡️ Coding Standards & Best Practices

1.  **Do not hardcode URLs in Step Definitions or Feature Files.**
    *   Base URLs belong in `ApiConfig.java` (or properties files). Feature files should only contain relative endpoints (e.g., `/auth/login`).
2.  **Avoid writing new step definitions if an existing one works.**
    *   Reuse generic steps like `the response body should match the following details:` instead of writing specific steps like `the user's name should be Jane`.
3.  **Tagging strategy.**
    *   Use logical tags at the Feature level (e.g., `@auth`, `@products`).
    *   Use status tags at the Scenario level (e.g., `@Positive`, `@Negative`).
4.  **Logging failures.**
    *   `Hooks.java` handles test teardown and automatically prints the failed API response body directly into the Cucumber HTML report for easy debugging. Do not clutter steps with generic `System.out.println()`.
5.  **Always use `ScenarioContext`.**
    *   Never use static variables in `ApiSteps.java` to pass state between steps, as it breaks parallel testing.