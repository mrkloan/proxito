# language: en
Feature: Get current date time

  Scenario: Get current date time through partner
    Given current date time is "1970-01-01T10:00:00.000+01:00"
    When a request is sent to "/datetime"
    Then "$.datetime" exists
#    Then "$.datetime" has value "1970-01-01T10:00:00.000000+0100"
