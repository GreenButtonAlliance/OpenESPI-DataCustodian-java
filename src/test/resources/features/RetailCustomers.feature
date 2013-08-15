Feature: Retail Customers
  As a Retail Customer,
  I want to be able to view my usage point in my browser
  So that I can see my UsagePoints

  Scenario: Retail Customer visits Retail Customer's home page
    Given I have a Retail Customer account

    When I log in as Alan Turing
    Then I should see Retail Customer home page

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am logged in as Grace Hopper

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Data Custodian creates customer
    Given I am a Data Custodian

    When I log in as Grace Hopper
    And I create a new retail customer with the name Grace Hopper
    Then I should see Grace Hopper in the customer list

  Scenario: Retail Customer views Usage Points
    Given a logged in retail customer

    When I look at my usage page
    Then I should see my Usage Points
