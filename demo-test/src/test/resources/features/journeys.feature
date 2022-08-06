# language: en
Feature: Find journeys

  Scenario: Find subways operated by RATP
    Given the parameter origin=2.29460;48.87358
    And the parameter destination=2.34893;48.85720
    And a departure at 10:00
    When a request is sent to "/journeys"
    Then there are some sections with a physical mode "Metro"
    And some of these sections have the network "RATP"
    And all of these sections have carbon emission data
