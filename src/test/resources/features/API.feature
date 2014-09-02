Feature: API
  As an API client,
  I should be able to access the Data Custodian API,
  So that I can access electric usage data from Data Custodian

  Scenario: API client requests a list of Usage Points
    Given a Retail Customer with Usage Points
    When I GET /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint
    Then I should receive the list of Usage Points

  Scenario: API client reads a Usage Point
    Given a Retail Customer with Usage Points

    And I log in as Retail Customer
    When I GET /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}
    Then I should receive the Usage Point

  Scenario: API client uploads a new Usage Point
    Given a Retail Customer
    When I POST /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint
    And I log in as Alan Turing
    Then I should see a new Usage Point

  Scenario: API client updates an existing Usage Point
    Given a Retail Customer with Usage Points
    When I POST /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint
    And I PUT /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}
    And I log in as Alan Turing
    Then I should see an updated Usage Point

  Scenario: API client deletes an existing Usage Point
    Given a Retail Customer with Usage Points
    And I DELETE /espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint/{usagePointId}
    Then the Usage Point should be deleted
