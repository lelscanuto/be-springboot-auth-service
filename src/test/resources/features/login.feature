Feature: User Login
  In order to access my account
  As a registered user
  I want to log in with valid credentials

    Scenario: Successful login as admin
      Given the system has a predefined admin account
      When the admin logs in using username "admin" and password "admin"
      Then the login should be successful

    Scenario: Failed login as admin
      Given the system has a predefined admin account
      When the admin logs in using username "admin" and password "wrong-password"
      Then the login should fail

