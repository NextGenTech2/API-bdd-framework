@notes @e2e
Feature: Notes API CRUD Operations

  Scenario: End-to-end flow to Create, Read, Update, Patch, and Delete a Note
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

    # 2. Create note using token, payload template, and data table parameterization
    Given the API endpoint is "/notes/api/notes"
    And the client sets the extracted token in the "x-auth-token" header
    # Note: Requires a new step definition to merge a JSON template with DataTable overrides
    And the request body is from the file "payloads/payloads.json" with the key "note_template" and parameterized with:
      | title       | description           | category |
      | My new note | This is my first note | Personal |
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | message                   | data.title  | data.description      | data.category |
      | true    | Note successfully created | My new note | This is my first note | Personal      |
    # Note: Requires a new step definition to save standard variables to ScenarioContext
    And the client extracts the "data.id" from the response and saves it as "noteId"

    # 3. Get All notes API
    Given the API endpoint is "/notes/api/notes"
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should contain "My new note"

    # 4. Get notes API by id
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | data.title  | data.description      | data.category |
      | My new note | This is my first note | Personal      |

    # 5. Update the notes using PUT call
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    And the request body is from the file "payloads/payloads.json" with the key "note_template" and parameterized with:
      | title           | description             | category | completed |
      | My updated note | This is my updated note | Home     | false     |
    When the client sends a PUT request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | message                   |
      | true    | Note successfully updated |

    # 6. Get Note by id and validate updated data
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | data.title      | data.description        | data.category | data.completed |
      | My updated note | This is my updated note | Home          | false          |

    # 7. Patch call to update specific fields
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    # Reusing existing dynamic DataTable step, perfect for partial PATCH payloads
    And the request body is formed from the following details:
      | completed |
      | true      |
    When the client sends a PATCH request
    Then the response status code should be 200

    # 8. Get Call and verify the data was patched
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | data.completed |
      | true           |

    # 9. Delete the notes
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a DELETE request
    Then the response status code should be 200
    And the response body should match the following details:
      | success | message                   |
      | true    | Note successfully deleted |

    # 10. Get that notes using GET API by note id and verify error
    Given the API endpoint is "/notes/api/notes/{id}"
    And the path parameters are:
      | id | {{noteId}} |
    And the client sets the extracted token in the "x-auth-token" header
    When the client sends a GET request
    Then the response status code should be 404
    And the response body should match the following details:
      | success | message                                                      |
      | false   | No note was found with the provided ID, Maybe it was deleted |