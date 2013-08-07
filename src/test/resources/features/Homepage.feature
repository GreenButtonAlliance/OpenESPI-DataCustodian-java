Feature: Homepage
  As Data Custodian,
  I should be able to access the home page,
  so I can verify that the web application is working

  Scenario: Data Custodian visits the home page
    Given I am Data Custodian

    When I navigate to the home page
    Then I should see "Welcome to Data Custodian application"