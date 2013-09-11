Feature: Retail Customers
  As a Retail Customer,
  I want to be able to view my usage point in my browser
  So that I can see my UsagePoints

  Scenario: Retail Customer visits Retail Customer's home page
    Given I have a Retail Customer account

    When I log in as Alan Turing
    Then I should see Retail Customer home page

  Scenario: Data Custodian views customer list
    Given I am a Data Custodian
    And there is an Alan Turing retail customer
    And I am logged in as Grace Hopper

    When I navigate to customer list page
    Then I should see Alan Turing in the customer list

  Scenario: Retail Customer views Usage Points
    Given a Retail Customer with Usage Points

    When I login as Retail Customer
    And I navigate to the Usage Points list

    Then I should see my Usage Points with title "Front Electric Meter"

  Scenario: Retail Customer views Meter Readings
    Given a Retail Customer with Usage Points

    When I navigate to the Usage Points list
    And I select Usage Point
    And I select Meter Reading

    Then I should see Meter Reading
    And I should see Reading Type
    And I should see Interval Blocks

  Scenario: Retail Customer downloads Usage Points in XML format
    Given a Retail Customer with Usage Points

    When I navigate to the Usage Points list
    Then I should be able to download Usage Points in XML format
    And the XML includes Service categories
    And the XML includes Meter Readings
    And the XML includes Reading Types
