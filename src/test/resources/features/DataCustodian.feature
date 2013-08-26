Feature: Retail Customers
  As a Data Custodian
  I want to be able to create retail customers
  So that I can associate retail customers to their usage data

  Scenario: Data Custodian visits Data Custodian's home page
    Given I have a Data Custodian account

    When I log in as Grace Hopper
    Then I should see Data Custodian home page

  Scenario: Data Custodian logs in with valid credentials
    Given I have a Data Custodian account

    When I log in as Grace Hopper
    Then I should be logged in

  Scenario: User logs in with invalid credentials
    Given I have a Data Custodian account

    When I log in as Grace Hopper with invalid credentials
    Then I should see login form

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am not logged in

    When I navigate to customer list page
    Then I should see login form

    When I log in as Grace Hopper
    Then I should see Alan Turing in the customer list

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am logged in as Grace Hopper

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Data Custodian uploads Usage Points
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I upload Usage Points

    When I login as Alan Turing
    Then I should see my Usage Points with title "Electric meter"
