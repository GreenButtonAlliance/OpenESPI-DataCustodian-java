Feature: API
  As a Third Party
  I want to access Retail Customer data
  So that I can display Retail Customer data

  Scenario: Third Party accesses Usage Points
    Given an authorized Third Party

    When I log in as Alan Turing
    And I access the Usage Points API
    Then I should see Usage Points
