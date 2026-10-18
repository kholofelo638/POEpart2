/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe.part2;

import java.util.regex.Pattern;

/**
 * Handles user registration and login for Part 1 of the PoE.
 *
 * The class stores the credentials captured during registration and then
 * verifies them again when the user attempts to log in.
 */
public class Login {

    // ---------------------------------------------------------------------
    // Message constants.
    // These are kept as constants so that the unit tests and the console
    // application always compare against exactly the same wording.
    // ---------------------------------------------------------------------
    public static final String USERNAME_SUCCESS =
            "Username successfully captured.";
    public static final String USERNAME_FAILURE =
            "Username is not correctly formatted; please ensure that your username "
            + "contains an underscore and is no more than five characters in length.";

    public static final String PASSWORD_SUCCESS =
            "Password successfully captured.";
    public static final String PASSWORD_FAILURE =
            "Password is not correctly formatted; please ensure that the password "
            + "contains at least eight characters, a capital letter, a number, "
            + "and a special character.";

    public static final String CELL_SUCCESS =
            "Cell number successfully captured.";
    public static final String CELL_FAILURE =
            "Cell number is incorrectly formatted or does not contain an "
            + "international code; please correct the number and try again.";

    public static final String LOGIN_FAILURE =
            "Username or password incorrect, please try again.";

    /**
     * Regular expression for a cell phone number that starts with an
     * international dialling code (a "+" followed by one to three digits)
     * and is then followed by the subscriber number, which may be no more
     * than ten characters long.
     *
     * Reference:
     * Baeldung, 2024. Validate Phone Numbers With Java Regex. [Online]
     * Available at: https://www.baeldung.com/java-regex-validate-phone-numbers
     * [Accessed 16 September 2026].
     */
    private static final Pattern CELL_PHONE_PATTERN =
            Pattern.compile("^\\+[0-9]{1,3}[0-9]{1,10}$");

    // ---------------------------------------------------------------------
    // Stored user details.
    // ---------------------------------------------------------------------
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    /** Set to true once loginUser() has verified the credentials. */
    private boolean loggedIn = false;

    public Login() {
    }

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Checks that the username contains an underscore and is no more than
     * five characters long.
     *
     * @param username the username entered by the user
     * @return true if the username is correctly formatted
     */
    public boolean checkUserName(String username) {
        if (username == null) {
            return false;
        }
        return username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks that the password is at least eight characters long and contains
     * a capital letter, a number and a special character.
     *
     * @param password the password entered by the user
     * @return true if the password meets the complexity rules
     */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapital = true;
            } else if (Character.isDigit(character)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(character)
                    && !Character.isWhitespace(character)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    /**
     * Checks that the cell phone number contains an international country code
     * and is correctly formatted, using the regular expression above.
     *
     * @param cellPhoneNumber the number entered by the user
     * @return true if the number is correctly formatted
     */
    public boolean checkCellPhoneNumber(String cellPhoneNumber) {
        if (cellPhoneNumber == null) {
            return false;
        }
        return CELL_PHONE_PATTERN.matcher(cellPhoneNumber.trim()).matches();
    }

    /**
     * Registers the user once all three values are valid, and returns the
     * message describing the outcome.
     *
     * @return the registration message for the first condition that fails, or
     *         the success message if the user was registered
     */
    public String registerUser(String username, String password, String cellPhoneNumber) {
        if (!checkUserName(username)) {
            return USERNAME_FAILURE;
        }

        if (!checkPasswordComplexity(password)) {
            return PASSWORD_FAILURE;
        }

        if (!checkCellPhoneNumber(cellPhoneNumber)) {
            return CELL_FAILURE;
        }

        // All three conditions have been met, so the details are stored.
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;

        return USERNAME_SUCCESS + " " + PASSWORD_SUCCESS + " " + CELL_SUCCESS;
    }

    /**
     * Verifies that the login details entered match the details stored when
     * the user registered.
     *
     * @return true when both the username and the password match
     */
    public boolean loginUser(String username, String password) {
        this.loggedIn = this.username != null
                && this.username.equals(username)
                && this.password != null
                && this.password.equals(password);

        return this.loggedIn;
    }

    /**
     * Returns the messaging for a successful or a failed login.
     *
     * @param loginSuccessful the result returned by loginUser()
     * @return the welcome message on success, otherwise the failure message
     */
    public String returnLoginStatus(boolean loginSuccessful) {
        if (loginSuccessful) {
            return "Welcome " + firstName + " " + lastName
                    + " it is great to see you again.";
        }
        return LOGIN_FAILURE;
    }

    // ---------------------------------------------------------------------
    // Getters and setters.
    // ---------------------------------------------------------------------
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}