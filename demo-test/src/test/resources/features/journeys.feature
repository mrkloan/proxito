# language: en
Feature: Find journeys

  Scenario Outline: Find subways operated by RATP: <label>
    Given the parameter origin=<origin>
    And the parameter destination=<destination>
    And a departure at 10:00
    When a request is sent to "/journeys"
    Then there are some sections with a physical mode "Metro"
    And some of these sections have the network "RATP"
    And all of these sections have carbon emission data
    And the json response matches

    Examples:
      | label                      | origin           | destination      |
      | Place d'Italie -> Châtelet | 2.29460;48.87358 | 2.34893;48.85720 |
      | Étoile -> Châtelet         | 2.35562;48.83133 | 2.34833;48.85811 |
