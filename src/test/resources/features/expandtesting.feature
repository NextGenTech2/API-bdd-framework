@expandTesting
Feature: API testing with POST GET DELETE PUT PATCH
  Scenario: API health check for GET
    Given the API endpoint is "/notes/api/health-check"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
        | success | status | message                   |
        | true    | 200    | Notes API is Running     |


  Scenario: User Registration, Login , Fetch Profile, update profile, vefify updated profile, logout
    # 1. Registration
    Given the API endpoint is "/notes/api/users/register"
    And the request body is from the file "payloads/payloads.json" with the key "user_registration"
    When the client sends a POST request
    Then the response status code should be 201
    And the response body should match the following details:
      | success | status | message                           | data.name | data.email    |
      | true    | 201    | User account created successfully | {{random.name}}     | {{random.email}} |

    # 2. Login
    Given the API endpoint is "/notes/api/users/login"
    And the request body is from the file "payloads/payloads.json" with the key "user_login"
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | status | message          | data.email       |
      | true    | 200    | Login successful | {{random.email}} |
    And the client extracts the "data.token" from the response as a Bearer token

    # 3. Get Profile
    Given the API endpoint is "/notes/api/users/profile"
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | status | message            | data.name       | data.email       |
      | true    | 200    | Profile successful | {{random.name}} | {{random.email}} |

    # 4. Update Profile via PATCH
    Given the API endpoint is "/notes/api/users/profile"
    And the client sets the extracted token in the "x-auth-token" header
    And the request body is formed from the following details:
      | name  | phone      | company |
      | Niraj | 4343843438 | test    |
    When the client sends a PATCH request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | status | message                      | data.name | data.phone | data.company |
      | true    | 200    | Profile updated successful   | Niraj     | 4343843438 | test         |

    # 5. Verify Update via GET
    Given the API endpoint is "/notes/api/users/profile"
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | status | message            | data.name | data.phone | data.company |
      | true    | 200    | Profile successful | Niraj     | 4343843438 | test         |

    # 6. Logout User
    Given the API endpoint is "/notes/api/users/logout"
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a DELETE request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | status | message                                |
      | true    | 200    | User has been successfully logged out |

@Negative
Scenario: Send invalid token in header
  Given the API endpoint is "/notes/api/users/profile"
  And the client sets an invalid token in the "x-auth-token" header 
  When the client sends a GET request
  Then the response status code should be 401
  And the response body should match the following details:
  | success | status | message                                                           | 
  | false    | 401    | Access token is not valid or has expired, you will need to login |

  Given the API endpoint is "/notes/api/users/login"
    And the request body is from the file "payloads/payloads.json" with the key "invalid_user_login"
    When the client sends a POST request
    Then the response status code should be 401
    And the response body should match the following details:
      | success | status | message                              |
      | false    | 401    | Incorrect email address or password |
    
