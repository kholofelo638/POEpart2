/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe.part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Login class, using the test data supplied in the PoE.
 */
class LoginTest {

    private Login login;

    private static final String VALID_USERNAME = "kyl_1";
    private static final String INVALID_USERNAME = "kyle!!!!!!!";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String INVALID_PASSWORD = "password";
    private static final String VALID_CELL = "+27838968976";
    private static final String INVALID_CELL = "08966553";

    @BeforeEach
    void setUp() {
        login = new Login("Kyle", "Smith");
    }

    // -----------------------------------------------------------------
    // Username
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Username is correctly formatted")
    void usernameCorrectlyFormatted() {
        assertTrue(login.checkUserName(VALID_USERNAME));
        assertEquals(Login.USERNAME_SUCCESS + " " + Login.PASSWORD_SUCCESS
                        + " " + Login.CELL_SUCCESS,
                login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL));
    }

    @Test
    @DisplayName("Username is incorrectly formatted")
    void usernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName(INVALID_USERNAME));
        assertEquals(Login.USERNAME_FAILURE,
                login.registerUser(INVALID_USERNAME, VALID_PASSWORD, VALID_CELL));
    }

    // -----------------------------------------------------------------
    // Password
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Password meets the complexity requirements")
    void passwordMeetsComplexity() {
        assertTrue(login.checkPasswordComplexity(VALID_PASSWORD));
    }

    @Test
    @DisplayName("Password does not meet the complexity requirements")
    void passwordDoesNotMeetComplexity() {
        assertFalse(login.checkPasswordComplexity(INVALID_PASSWORD));
        assertEquals(Login.PASSWORD_FAILURE,
                login.registerUser(VALID_USERNAME, INVALID_PASSWORD, VALID_CELL));
    }

    // -----------------------------------------------------------------
    // Cell phone number
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Cell phone number is correctly formatted")
    void cellPhoneCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber(VALID_CELL));
    }

    @Test
    @DisplayName("Cell phone number is incorrectly formatted")
    void cellPhoneIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber(INVALID_CELL));
        assertEquals(Login.CELL_FAILURE,
                login.registerUser(VALID_USERNAME, VALID_PASSWORD, INVALID_CELL));
    }

    // -----------------------------------------------------------------
    // Login
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Login successful")
    void loginSuccessful() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);

        assertTrue(login.loginUser(VALID_USERNAME, VALID_PASSWORD));
        assertEquals("Welcome Kyle Smith it is great to see you again.",
                login.returnLoginStatus(true));
    }

    @Test
    @DisplayName("Login failed")
    void loginFailed() {
        login.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL);

        assertFalse(login.loginUser(VALID_USERNAME, "WrongPass1!"));
        assertEquals(Login.LOGIN_FAILURE, login.returnLoginStatus(false));
    }
}