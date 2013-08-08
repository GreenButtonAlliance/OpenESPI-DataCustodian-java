Feature: Retail Customers
  As a Data Custodian
  I want to be able to create retail customers
  So that I can associate retail customers to their usage data

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list
