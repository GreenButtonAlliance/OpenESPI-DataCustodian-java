Feature: Usage Point Import
  As a Data Custodian
  I want to be able to import XML UsagePoint data
  So that my Retail Customers can download their UsagePoint data

  Scenario: Data Custodian Imports Retail Customer Data
    Given Alan Turing Retail Customer

    When I import Alan Turing's Usage Points from an XML file
    And I login as Alan Turing
    And I navigate to the Usage Points list
    And I select Usage Point
    And I select Meter Reading

    Then I should see Meter Reading
    And I should see Reading Type
    And I should see Interval Blocks
