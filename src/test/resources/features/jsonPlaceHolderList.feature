@currentList
Feature: API Tests

  Scenario Outline: Validate user details for ID <id>
    Given the API endpoint is "/users"
    And the query parameters are:
      | id | <id> |
    When the client sends a GET request
    Then the response status code should be 200
    And the response body should match the following details:
      | id   | name   | username   | email   | address.street   | address.suite   | address.city   | address.zipcode   | address.geo.lat   | address.geo.lng   | phone   | website   | company.name   | company.catchPhrase   | company.bs   |
      | <id> | <name> | <username> | <email> | <street>         | <suite>         | <city>         | <zip>             | <lat>             | <lng>             | <phone> | <website> | <cName>        | <phrase>              | <bs>         |

    Examples:
      | id | name             | username  | email                     | street        | suite     | city        | zip        | lat      | lng       | phone               | website       | cName         | phrase                                     | bs                                  |
      | 4  | Patricia Lebsack | Karianne  | Julianne.OConner@kory.org | Hoeger Mall   | Apt. 692  | South Elvis | 53919-4257 | 29.4572  | -164.2990 | 493-170-9623 x156   | kale.biz      | Robel-Corkery | Multi-tiered zero tolerance productivity    | transition cutting-edge web services|
      | 2  | Ervin Howell     | Antonette | Shanna@melissa.tv         | Victor Plain | Suite 87 | Wisokyburgh | 90566-7771 | -43.9509 | -34.4618  | 010-692-6593 x09125 | anastasia.net | Deckow-Crist  | Proactive didactic contingency             | synergize scalable supply-chains    |