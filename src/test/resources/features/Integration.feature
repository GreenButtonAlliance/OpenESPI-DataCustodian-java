Feature: System Integration

  Scenario: Data Custodian should be able to create a Retail Customer
    Given I have a Data Custodian account

    When I create a Retail Customer
    Then a Retail Customer should be created

  Scenario: Import Usage Point
    When I import Usage Point
    Then Usage Point should be save in the database
    And Meter Readings should be save in the database
    And Interval Blocks should be save in the database

  Scenario: Import and export Usage Point
    When I import Usage Point
    And I export Usage Point
    Then export data should include Feed

