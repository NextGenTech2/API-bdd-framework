# 🚀 RestAssured BDD API Automation Framework

A robust, scalable, and easy-to-use API test automation framework built with **Java**, **RestAssured**, and **Cucumber BDD**. 

This framework is designed to abstract away complex Java code, allowing SDETs and QA Engineers to write comprehensive end-to-end API tests using highly reusable Gherkin steps, dynamic data generation, and centralized JSON payloads.

## 🛠️ Tech Stack

*   **Language**: Java (11+)
*   **BDD Framework**: Cucumber
*   **HTTP Client**: RestAssured
*   **Test Runner**: JUnit 4
*   **JSON Processing**: `org.json` & `JsonPath`
*   **Reporting**: Masterthought Cucumber Reports

## ✨ Key Features

1. **Zero-Code Step Definitions**: Highly reusable, generic Cucumber steps mean you rarely have to write new Java code to automate new endpoints.
2. **Centralized Payload Management**: JSON requests are stored in a clean `payloads.json` file rather than being hardcoded in step definitions or feature files.
3. **Dynamic Data Generation**: Built-in support for generating random data on the fly (e.g., `{{random.email}}`, `{{random.name}}`) directly within your JSON templates.
4. **Payload Parameterization**: Override template JSON fields directly from your Gherkin `DataTable` for elegant data-driven testing.
5. **State Management**: Safely extract IDs and Tokens from responses and pass them to subsequent API calls using a Thread-safe `ScenarioContext`.
6. **Detailed Request Logging**: Automatically prints beautifully formatted Request (Method, URL, Headers, Body) and Response details to the console for easy debugging.

---

## 📂 Project Structure

```text
API-AUTOMATION/
├── src/main/java/com/example/api/
│   ├── client/       # RestClient.java (Wrapper around RestAssured methods)
│   ├── models/       # POJOs and RequestBuilder
│   ├── services/     # ApiService.java (Prepares request specs and logs requests)
│   └── utils/        # Core utilities: ApiConfig, AuthManager, ResponseValidator, ScenarioContext
├── src/test/java/com/example/
│   ├── runners/      # ApiRunnerTest.java (JUnit runner configuration & tags)
│   └── steps/        # Hooks.java & ApiSteps.java (Cucumber Step Definitions)
├── src/test/resources/
│   ├── features/     # Cucumber .feature files (Gherkin scenarios)
│   └── payloads/     # payloads.json (Centralized test data / JSON bodies)
```

---

## 🚀 Getting Started

### Prerequisites
* Java JDK 11 or higher installed.
* Maven installed and configured in your system `PATH`.
* Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code).

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/api-automation.git
   ```
2. Navigate to the project directory:
   ```bash
   cd api-automation
   ```
3. Install dependencies:
   ```bash
   mvn clean install -DskipTests
   ```

---

## 🧪 Running the Tests

You can execute the tests using Maven from the command line.

**Run all tests:**
```bash
mvn clean test
```

**Run a specific suite using Cucumber tags (e.g., `@e2e`):**
```bash
mvn clean test -Dcucumber.filter.tags="@e2e"
```

---

## 📊 Test Reporting

The framework is integrated with **Masterthought Cucumber Reports**. 
Once the test execution finishes, a rich HTML report is automatically generated.

* **Location:** `target/cucumber-reports/cucumber-html-reports/overview-features.html`
* **Failure Logging:** If a test fails, `Hooks.java` automatically intercepts the failure and embeds the actual API response body directly into the HTML report!

---

## 💡 How to Write a Test

Writing a new test is as simple as adding data and writing Gherkin.

### 1. Add Payload Template (`src/test/resources/payloads/payloads.json`)
```json
"note_template": {
  "title": "{{title}}",
  "description": "{{description}}",
  "category": "{{category}}"
}
```

### 2. Write the Scenario (`src/test/resources/features/notes.feature`)
```gherkin
@notes @e2e
Feature: Notes API Operations

  Scenario: Create a note and verify response
    # 1. Login and save the token
    Given the API endpoint is "/notes/api/users/login"
    And the request body is from the file "payloads/payloads.json" with the key "user_login"
    When the client sends a POST request
    Then the response status code should be 200
    And the client extracts the "data.token" from the response as a Bearer token

    # 2. Inject token, parameterize the payload, and send POST
    Given the API endpoint is "/notes/api/notes"
    And the client sets the extracted token in the "x-auth-token" header
    And the request body is from the file "payloads/payloads.json" with the key "note_template" and parameterized with:
      | title       | description           | category |
      | My new note | This is my first note | Personal |
    When the client sends a POST request
    
    # 3. Validate
    Then the response status code should be 200
    And the response body should match the following details:
      | success | data.title  | data.category |
      | true    | My new note | Personal      |
```

---

## 📝 Coding Standards

* **No Hardcoded URLs**: Base URLs belong in `ApiConfig.java`. Use relative paths in Feature files.
* **Use `ScenarioContext`**: Do not use static variables in Step Definitions. Use `ScenarioContext.setData(key, value)` and `ScenarioContext.getData(key)` to safely share state between steps.
* **Dynamic Type Conversion**: The framework automatically parses strings like `"true"` and `"false"` in DataTables into JSON Booleans, and numeric strings into JSON Integers.

---

## 🤝 Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
