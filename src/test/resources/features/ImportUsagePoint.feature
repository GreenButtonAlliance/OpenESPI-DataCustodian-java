Feature: Import Usage Point
  As a Data Custodian
  I want to be able to import XML UsagePoint data
  So that my Retail Customers can download their UsagePoint data

  Scenario: Data Custodian Imports Retail Customer Data
    Given a Retail Customer

    When I login as Grace Hopper
    And I associate "Front Electric Meter" Usage Point with Retail Customer
    And I import Usage Point from XML

    When I log in as Retail Customer
    And I navigate to the Usage Points list
    And I select a "Front Electric Meter" Usage Point
    Then I should see my Electric Power Usage Summaries
