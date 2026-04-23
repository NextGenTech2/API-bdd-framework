@booker
Feature: Health Check API

  Scenario: Validate Health Check API
    Given the API endpoint is "/ping"
    When the client sends a GET request
    Then the response status code should be 201
    And the response should contain the text "Created"

  @PositiveAuth
  Scenario: Generate valid authentication token
    Given the API endpoint is "/auth"
    And the request body is from the file "payloads/payloads.json" with the key "valid_auth"
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should contain "token"

  @NegativeAuth
  Scenario: Fail authentication with invalid credentials
    Given the API endpoint is "/auth"
    And the request body is from the file "payloads/payloads.json" with the key "invalid_auth"
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should contain "Bad credentials"

  @PositiveBookingSearch
  Scenario: Get Existing Booking Details
    Given the API endpoint is "/booking/1"
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | firstname | lastname | totalprice | bookingdates.checkin |
      | Mark      | Wilson   |        399 |           2017-03-07 |

  @NegativeBookingSearch
  Scenario: Get Booking with Non-Existent ID
  # Using an ID that likely doesn't exist
    Given the API endpoint is "/booking/999999"
    When the client sends a GET request
    Then the response status code should be 404
    And the response body should contain "Not Found"

  @PositiveCreateBooking
  Scenario: Create a New Booking
    Given the API endpoint is "/booking"
    And the request body is:
      """
      {
          "firstname" : "Niraj",
          "lastname" : "Kumar",
          "totalprice" : 111,
          "depositpaid" : true,
          "bookingdates" : {
              "checkin" : "2026-01-01",
              "checkout" : "2026-01-05"
          },
          "additionalneeds" : "Lunch"
      }
      """
    When the client sends a POST request
    Then the response status code should be 200
    And the response body should contain "bookingid"

  @NegativeCreateBooking
  Scenario: Create Booking with Missing Required Fields
    Given the API endpoint is "/booking"
    And the request body is:
      """
      {
          "firstname" : "Jim"
      }
      """
    When the client sends a POST request
  # Most APIs return 400 Bad Request or 500 Internal Server Error for missing fields
    Then the response status code should be 500
    And the response body should contain "Internal Server Error"
