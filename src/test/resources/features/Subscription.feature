Feature: Subscription
  As a Third Party
  I want to access Retail Customer data
  So that I can display Retail Customer data

  Scenario: Third Party accesses Usage Points
    Given a Retail Customer with Usage Points

    When I access the Usage Points API
    Then I should see Usage Points
