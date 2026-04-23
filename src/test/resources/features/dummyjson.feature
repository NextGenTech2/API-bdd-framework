@dummyjson
Feature: DummyJSON Test API Validation

  Scenario: Validate GET method on test endpoint
    Given the API endpoint is "/test"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | status | method |
      | ok     | GET    |

  Scenario: Validate POST method on test endpoint
    Given the API endpoint is "/test"
    And the request body is:
      """
      {}
      """
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should match the following details:
      | status | method |
      | ok     | POST   |

  Scenario: Validate PUT method on test endpoint
    Given the API endpoint is "/test"
    And the request body is:
      """
      {}
      """
    When the client sends a PUT request
    Then the response status code should be 200
    And the response body should match the following details:
      | status | method |
      | ok     | PUT    |

  Scenario: Validate DELETE method on test endpoint
    Given the API endpoint is "/test"
    When the client sends a DELETE request
    Then the response status code should be 200
    And the response body should match the following details:
      | status | method |
      | ok     | DELETE |

  Scenario: Validate PATCH method on test endpoint
    Given the API endpoint is "/test"
    And the request body is:
      """
      {}
      """
    When the client sends a PATCH request
    Then the response status code should be 200
    And the response body should match the following details:
      | status | method |
      | ok     | PATCH  |
    