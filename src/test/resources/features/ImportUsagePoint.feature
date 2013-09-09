Feature: Usage Point Import
  As a Data Custodian
  I want to be able to import XML UsagePoint data
  So that my Retail Customers can download their UsagePoint data

  Scenario: Data Custodian Imports Retail Customer Data
    Given Alan Turing Retail Customer

    When I import Alan Turing's Usage Points from an XML file
    Then Alan Turing should be able to see the imported data
