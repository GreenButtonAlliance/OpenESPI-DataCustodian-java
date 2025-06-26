Feature: Authentication
  Data Custodians should only have access to the home page and pages under /custodian folder.
  Retail Customers should only have access to the home page and pages under /customer folder.

  Scenario: Data Custodian visits Data Custodian's home page
    Given I have a Data Custodian account

    When I log in as a Data Custodian
    Then I should see Data Custodian home page

  Scenario: Retail Customer visits Retail Customer's home page
    Given I have a Retail Customer account

    When I log in as Alan Turing
    Then I should see Retail Customer home page
