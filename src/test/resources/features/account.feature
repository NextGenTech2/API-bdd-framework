@proxy @account
Feature: Account API Functionality

  Scenario: Service Health Check
    Given the API endpoint is "/health"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_health_check_schema.json"
    And the response body should match the following details:
      | status | message                    |
      | Active | Service is up and running  |

  Scenario: Generate Bearer Token and Perform Full Account Lifecycle (CRUD & History)
    # 0. Set dynamic client number for sandboxing
    And the client sets the header "Client-Number" to "{{random.client}}"

    # 1. Generate Auth Token
    Given the API endpoint is "/auth/token"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "auth_token_schema.json"
    And the response body should match the following details:
      | token_type |
      | Bearer     |
    And the client extracts the "access_token" from the response as a Bearer token

    # 2. Create Account (JWT token is set in header once at start)
    Given the API endpoint is "/accounts"
    And the client sets the Bearer token in the header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "create_account"
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_action_status_schema.json"
    And the response body should match the following details:
      | status    |
      | Completed |

    # 3. Retrieve list to extract the generated Account ID
    Given the API endpoint is "/accounts"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "accounts_list_schema.json"
    And the client extracts the "data[0].identifiers[0].value" from the response as a Bearer token

    # 4. Get Account Details using header Account-Id (Authorization header is automatically maintained)
    Given the API endpoint is "/accounts"
    And the client sets the extracted token in the "Account-Id" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_details_schema.json"
    And the response body should match the following details:
      | data[0].status |
      | Active         |

    # 5. Get Account List with Pagination query params
    Given the API endpoint is "/accounts"
    And the query parameters are:
      | pageNumber | 2 |
      | limit      | 5 |
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "accounts_paginated_list_schema.json"
    And the response body should match the following details:
      | metadata.pageNumber | metadata.pageSize |
      | 2                   | 5                 |

    # 6. Update Account details using PUT call
    Given the API endpoint is "/accounts"
    And the client sets the extracted token in the "Account-Id" header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "update_account"
    When the client sends a PUT request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_action_status_schema.json"
    And the response body should match the following details:
      | status    |
      | Completed |

    # 7. Retrieve Account History
    Given the API endpoint is "/accountHistory"
    And the client sets the extracted token in the "Account-Id" header
    And the query parameters are:
      | page-number | 1 |
      | limit       | 5 |
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_history_schema.json"
    And the response body should match the following details:
      | data.changes[0].changeType | data.changes[0].changeFrom | data.changes[0].changeTo                             | metadata.pageNumber | metadata.pageSize |
      | Account Name               | ESG                        | Exchange ESG Focus Fund                            | 1                   | 5                 |

    # 8. Delete Account
    Given the API endpoint is "/accounts"
    And the client sets the extracted token in the "Account-Id" header
    When the client sends a DELETE request
    Then the response status code should be 200
    And the response body should match the JSON schema "account_action_status_schema.json"
    And the response body should match the following details:
      | status    |
      | Completed |

  Scenario: Attempt GET Accounts Details with Invalid Authorization Token
    Given the API endpoint is "/accounts"
    And the client sets the header "Client-Number" to "{{random.client}}"
    And the client sets an invalid Bearer token in the header
    When the client sends a GET request
    Then the response status code should be 403

  Scenario: Attempt PUT Account with Invalid Account-Id
    # 0. Set dynamic client number for sandboxing
    And the client sets the header "Client-Number" to "{{random.client}}"

    # 1. Generate token
    Given the API endpoint is "/auth/token"
    When the client sends a GET request
    Then the response status code should be 200
    And the client extracts the "access_token" from the response as a Bearer token

    # 2. Make call with non-existent Account-Id header
    Given the API endpoint is "/accounts"
    And the client sets the Bearer token in the header
    And the client sets an invalid token in the "Account-Id" header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "update_account"
    When the client sends a PUT request
    Then the response status code should be 400
