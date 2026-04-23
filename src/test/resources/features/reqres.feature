@reqres 
Feature: ReqRes API Testing Suite
    @Get
    Scenario: Validate Single User Fetch
        Given the API endpoint is "/users/2"
        When the client sends a GET request
        Then the response status code should be 200
        And the response body should match the following details:
        | data.id | data.first_name | data.last_name |
        | 2       | janet           | Weaver         |

  @Post
    Scenario: Create A New User
        Given the API endpoint is "/users"
        # Updated path below:
        And the request body is from the file "payloads/payloads.json" with the key "create_user"
        When the client sends a POST request
        Then the response status code should be 201
        And the response body should contain "Neeraj"
    
     @Put
    Scenario: Update A User
    # Add the ID /2 to the endpoint path
    Given the API endpoint is "/users/2"  
    And the request body is:
      """
      {
        "name": "Neeraj",
        "job": "Senior Engineering Manager"
      }
      """
    When the client sends a PUT request
    Then the response status code should be 200
      
    @Delete
    Scenario: Delete A User
        Given the API endpoint is "/users/2"
        When the client sends a DELETE request
        Then the response status code should be 204

