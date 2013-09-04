Feature: Usage Point Import
  As a Data Custodian
  I want to be able to import XML UsagePoint data
  So that my Retail Customers can download their UsagePoint data

  Scenario: Data Custodian Imports Retail Customer Data
    Given an XML file
    And there is an Alan Turing retail customer
    And a logged in retail customer

    When Data Custodian imports the XML file
    Then the import tool should indicate success

    When I look at my usage page
    Then I should see the imported data