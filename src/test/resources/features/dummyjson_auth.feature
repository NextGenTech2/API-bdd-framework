@auth
Feature: DummyJSON Authentication and Authorization Flow

  @Positive
  Scenario: Validate successful login and fetching protected user profile
    # 1st Request: Login to get the JWT Token
    Given the API endpoint is "/auth/login"
    And the request body is from the file "payloads/payloads.json" with the key "dummyjson_valid_login"
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should match the following details:
      | username | email                         | firstName |
      | emilys   | emily.johnson@x.dummyjson.com | Emily     |
    And the client extracts the "accessToken" from the response as a Bearer token

    # 2nd Request: Use the token to fetch protected data
    Given the API endpoint is "/auth/me"
    And the client sets the Bearer token in the header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | id | username | email                         | firstName | lastName |
      | 1  | emilys   | emily.johnson@x.dummyjson.com | Emily     | Johnson  |

  @Negative
  Scenario: Validate login failure with invalid credentials
    Given the API endpoint is "/auth/login"
    And the request body is from the file "payloads/payloads.json" with the key "dummyjson_invalid_login"
    When the client sends a POST request
    Then the response status code should be 400
    And the response body should contain "Invalid credentials"

  @Negative
  Scenario: Validate access to protected route without an Authorization token
    Given the API endpoint is "/auth/me"
    When the client sends a GET request
    Then the response status code should be 401
    And the response body should contain "Token"

  @Negative
  Scenario: Validate access to protected route with an invalid token
    Given the API endpoint is "/auth/me"
    And the client sets an invalid Bearer token in the header
    When the client sends a GET request
    Then the response status code should be 500
    And the response body should contain "invalid signature"

@categories
  @Positive
  Scenario: Successfully fetch the list of product categories
    Given the API endpoint is "/products/category-list"
    When the client sends a GET request
    Then the response status code should be 200
    # Utilizing your existing string match step
    And the response body should contain "beauty"
    And the response body should contain "furniture"

  @Positive
  Scenario: Validate the response contains specific categories using a list
    Given the API endpoint is "/products/category-list"
    When the client sends a GET request
    Then the response status code should be 200
    # This is a new suggested step to strictly validate array items
    And the response array should contain the following items:
      | fragrances     |
      | beauty         |
      | groceries      |
      | furniture      |

  @Negative
  Scenario: Attempt to fetch categories with an invalid HTTP method
    Given the API endpoint is "/products/category-list"
    # Endpoints typically only support specific methods. Sending a POST to a GET-only route should fail.
    When the client sends a POST request
    Then the response status code should be 404
    And the response body should contain "Error"

  @Negative
  Scenario: Attempt to fetch categories using an incorrect endpoint path
    Given the API endpoint is "/products/category-list-invalid"
    When the client sends a GET request
    Then the response status code should be 404
    And the response body should contain "category-list-invalid"
