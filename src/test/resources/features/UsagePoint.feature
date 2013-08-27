Feature: Usage Points
  As a Third Party
  I want to access Usage Points from the Data Custodian via a rest interface
  So that I can retrieve Usage Point information

  Scenario: Third Party requests Usage Points
    Given I am a Third Party
    And there exists a user that has Usage Points

    When I request the usage points for a user
    Then I should receive an xml response with the user's usage points

  Scenario: Third party requests a specific usage point
    Given I am a Third Party
    And there exists a user that has Usage Points

    When I request a usage point for a user
    Then I should receive an xml response with the usage point
