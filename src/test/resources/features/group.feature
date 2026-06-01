@proxy @group
Feature: Group API Functionality

  Scenario: Generate Bearer Token, Create Group and Associate Account Details
    # 0. Set dynamic client number for sandboxing
    And the client sets the header "Client-Number" to "{{random.client}}"

    # 1. Generate Auth Token
    Given the API endpoint is "/auth/token"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | token_type |
      | Bearer     |
    And the client extracts the "access_token" from the response as a Bearer token

    # 2. Create Group (Authorization header is set once)
    Given the API endpoint is "/groups"
    And the client sets the Bearer token in the header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "create_group"
    When the client sends a POST request
    Then the response status code should be 201
    And the response body should match the following details:
      | status | identifiers[0].type | identifiers[0].value   |
      | Active | GroupName           | North America Accounts |
    # Extract generated GroupId and save it
    And the client extracts the "identifiers[1].value" from the response as a Bearer token

    # 3. Fetch Groups List with limit (Authorization header remains valid)
    Given the API endpoint is "/groups"
    And the query parameters are:
      | groupType | ACCOUNT_GROUP |
      | limit     | 10            |
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | metadata.pageSize |
      | 10                |

    # 4. Associate Items (GroupDetails) to Group
    Given the API endpoint is "/groupDetails"
    And the client sets the extracted token in the "Group-Id" header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "add_group_details"
    When the client sends a POST request
    Then the response status code should be 201
    And the response body should match the following details:
      | data[0].status | data[0].type | data[0].identifiers[0].type | data[0].identifiers[0].value |
      | Active         | Account      | AccountId                   | {{random.name}}              |

    # 5. Fetch Group Details and Assert Association
    Given the API endpoint is "/groupDetails"
    And the client sets the extracted token in the "Group-Id" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | data[0].status | data[0].type | data[0].identifiers[0].type | data[0].identifiers[0].value |
      | Active         | Account      | AccountId                   | {{random.name}}              |

  Scenario: Attempt to Retrieve Details for Non-existent Group
    # 0. Set dynamic client number for sandboxing
    And the client sets the header "Client-Number" to "{{random.client}}"

    # 1. Generate Auth Token
    Given the API endpoint is "/auth/token"
    When the client sends a GET request
    Then the response status code should be 200
    And the client extracts the "access_token" from the response as a Bearer token

    # 2. Try fetching group details with non-existent Group-Id
    Given the API endpoint is "/groupDetails"
    And the client sets the Bearer token in the header
    And the client sets an invalid token in the "Group-Id" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | metadata.totalElements |
      | 0                      |

  Scenario: Attempt to Create Group Details on Non-existent Group
    # 0. Set dynamic client number for sandboxing
    And the client sets the header "Client-Number" to "{{random.client}}"

    # 1. Generate Auth Token
    Given the API endpoint is "/auth/token"
    When the client sends a GET request
    Then the response status code should be 200
    And the client extracts the "access_token" from the response as a Bearer token

    # 2. Try adding items with non-existent Group-Id
    Given the API endpoint is "/groupDetails"
    And the client sets the Bearer token in the header
    And the client sets an invalid token in the "Group-Id" header
    And the request body is from the file "payloads/proxy_payloads.json" with the key "add_group_details"
    When the client sends a POST request
    Then the response status code should be 400
