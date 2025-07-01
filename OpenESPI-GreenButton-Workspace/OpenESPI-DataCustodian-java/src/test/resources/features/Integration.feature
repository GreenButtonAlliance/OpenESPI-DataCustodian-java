Feature: Integration

  @spring
  Scenario: Data Custodian should be able to create a Retail Customer
    Given I have a Data Custodian account

    When I create a Retail Customer
    Then a Retail Customer should be created

  @spring
  Scenario: Import Usage Point
    When I import Usage Point
    Then Usage Point should be saved in the database
    And Meter Readings should be saved in the database
    And Reading Type should be saved in the database
    And Interval Blocks and Readings should be saved in the database
    And Electric Power Usage Summary should be saved in the database

  @spring
  Scenario: Import and export Usage Point
    When I import Usage Point
    And I export Usage Point
    Then export data should include Feed

