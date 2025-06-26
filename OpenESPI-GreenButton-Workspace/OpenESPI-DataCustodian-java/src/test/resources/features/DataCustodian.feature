Feature: Data Custodian
  As a Data Custodian
  I want to be able to create retail customers
  So that I can associate retail customers to their usage data

  Scenario: Data Custodian visits Data Custodian's home page
    Given I have a Data Custodian account

    When I log in as a Data Custodian
    Then I should see Data Custodian home page

  Scenario: Data Custodian logs in with valid credentials
    Given I have a Data Custodian account

    When I log in as a Data Custodian
    Then I should be logged in

  Scenario: User logs in with invalid credentials
    Given I have a Data Custodian account

    When I log in as a Data Custodian with invalid credentials
    Then I should see login form

  Scenario: Data Custodian creates customer
    Given I am a Data Custodian

    When I log in as a Data Custodian
    And I create a new Retail Customer
    Then I should see the new Retail Customer in the customer list

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And Alan Turing is a Retail Customer
    And I log in as a Data Custodian

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Unauthenticated Data Custodian views customer list
    Given I am a Data Custodian
    And Alan Turing is a Retail Customer
    And I am not logged in

    When I navigate to customer list page
    Then I should see login form

  Scenario: Data Custodian views Retail Customer profile page
    When I log in as a Data Custodian
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    Then I should see "Alan Turing" profile page

  Scenario: Data Custodian associates a Usage Point with Retail Customer
    Given a Retail Customer

    When I log in as a Data Custodian
    And I associate "Back Electric Meter" Usage Point with Retail Customer

    When I log in as Retail Customer
    And I navigate to the Usage Points list
    Then I should see "Back Electric Meter"

  Scenario: Data Custodian uploads Retail Customer usage information
    Given a Retail Customer

    When I log in as a Data Custodian
    And I associate "Front Electric Meter" Usage Point with Retail Customer
    And I upload Usage Points

    When I log in as Retail Customer
    Then the logged in retail customer can see their usage data
