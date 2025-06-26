Feature: Import Usage Point
  As a Data Custodian
  I want to be able to import XML UsagePoint data
  So that my Retail Customers can download their UsagePoint data

  Scenario: Data Custodian Imports Retail Customer Data
    Given a Retail Customer

    When I log in as a Data Custodian
    And I associate "Front Electric Meter" Usage Point with Retail Customer
    And I upload Usage Points

    When I log in as Retail Customer
    Then the logged in retail customer can see their usage data
