Feature: Data Custodian
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

  Scenario: Data Custodian creates customer
    Given I am a Data Custodian

    When I log in as Grace Hopper
    And I create a new Retail Customer
    Then I should see the new Retail Customer in the customer list

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am logged in as Grace Hopper

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Unauthenticated Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am not logged in

    When I navigate to customer list page
    Then I should see login form

  Scenario: Data Custodian views Retail Customer profile page
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    Then I should see "Alan Turing" profile page

  Scenario: Data Custodian uploads Usage Points
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    And I upload Usage Points

    When I login as Alan Turing
    And I navigate to the Usage Points list
    Then I should see my Usage Points with title "Front Electric Meter"

  Scenario: Data Custodian uploads Usage Points with Service Categories
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    And I upload Usage Points

    When I login as Alan Turing
    And I navigate to the Usage Points list
    Then I should see my Usage Points with Service Categories with Service Kind of "ELECTRICITY_SERVICE"

  Scenario: Data Custodian uploads Usage Points with Meter Readings
    Given a Retail Customer with Usage Points

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select Retail Customer from customer list
    And I upload Usage Points

    When I login as Retail Customer
    And I navigate to the Usage Points list
    And I select "Front Electric Meter" from the Usage Point list
    Then I should see the Meter Readings

  Scenario: Data Custodian uploads Usage Points with Interval Blocks
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    And I upload Usage Points

    When I login as Alan Turing
    And I navigate to the Usage Points list
    And I select a Usage Point
    And I select Meter Reading
    Then I should see my Meter Reading with Interval Blocks

  Scenario: Data Custodian uploads Usage Points with Electric Power Usage Summaries
    Given Grace Hopper Data Custodian
    And Alan Turing Retail Customer

    When I login as Grace Hopper
    And I navigate to customer list page
    And I select "Alan Turing" from customer list
    And I upload Usage Points

    When I login as Alan Turing
    And I navigate to the Usage Points list
    And I select a Usage Point
    Then I should see my Electric Power Usage Summaries
