@old
Feature: API Tests

  Scenario: Get a list of accounts
    Given the API endpoint is "/todos/1"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should contain "completed"

  Scenario: Create a new account
    Given the API endpoint is "/accounts"
    And the request body is:
      """
      {
        "name": "John Doe",
        "email": "john.doe@example.com",
        "balance": 1000
      }
      """
    When the client sends a POST request
    Then the response status code should be 201
    And the response body should contain "account"