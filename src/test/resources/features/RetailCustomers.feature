Feature: Retail Customers
  As a Data Custodian
  I want to be able to create retail customers
  So that I can associate retail customers to their usage data

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am logged in as Grace Hopper

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Data Custodian creates customer
    Given I am a Data Custodian

    When I create a new retail customer with the name Grace Hopper
    Then I should see Grace Hopper in the customer list
