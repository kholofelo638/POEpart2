/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe.part2;

import java.util.Scanner;

/**
 * Console application for Parts 1 and 2 of the PoE.
 *
 * The user first registers an account and logs in (Part 1). Only after a
 * successful login can they use QuickChat to send messages (Part 2). No GUI
 * components are used.
 */
public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private static final int MENU_SEND_MESSAGES = 1;
    private static final int MENU_RECENT_MESSAGES = 2;
    private static final int MENU_QUIT = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("        REGISTRATION AND LOGIN FEATURE        ");
        System.out.println("==============================================");

        Login login = register(scanner);
        boolean loggedIn = login(scanner, login);

        // Messages may only be sent once the user has logged in successfully.
        if (loggedIn) {
            runQuickChat(scanner);
        } else {
            System.out.println("\nYou must log in successfully before you can send messages.");
        }

        System.out.println("\nGoodbye!");
        scanner.close();
    }

    // =====================================================================
    // Part 1: registration and login
    // =====================================================================

    /**
     * Captures the user's details and keeps asking until every value entered
     * is valid, then returns the Login object holding those details.
     */
    private static Login register(Scanner scanner) {
        System.out.println("\n--- REGISTER ---");

        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine().trim();

        Login login = new Login(firstName, lastName);

        boolean registered = false;

        while (!registered) {
            System.out.print("\nEnter a username (must contain an underscore, "
                    + "max five characters): ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter a password (min eight characters, with a capital "
                    + "letter, a number and a special character): ");
            String password = scanner.nextLine().trim();

            System.out.print("Enter your cell phone number (e.g. +27838968976): ");
            String cellPhoneNumber = scanner.nextLine().trim();

            String message = login.registerUser(username, password, cellPhoneNumber);
            System.out.println("\n" + message);

            // Registration only succeeds when the details were actually stored.
            registered = login.getUsername() != null;

            if (!registered) {
                System.out.println("Please try again.");
            }
        }

        System.out.println("\nYou have been registered successfully.");
        return login;
    }

    /**
     * Asks the user for their username and password and reports whether the
     * login was successful. The user gets three attempts.
     *
     * @return true if the user logged in successfully
     */
    private static boolean login(Scanner scanner, Login login) {
        System.out.println("\n--- LOGIN ---");

        int attempts = 0;
        boolean successful = false;

        while (attempts < MAX_LOGIN_ATTEMPTS && !successful) {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            successful = login.loginUser(username, password);
            System.out.println("\n" + login.returnLoginStatus(successful));

            attempts++;
        }

        if (!successful) {
            System.out.println("\nYou have used all three login attempts.");
        }

        return successful;
    }

    // =====================================================================
    // Part 2: QuickChat
    // =====================================================================

    /**
     * Shows the welcome message, asks how many messages the user wants to
     * enter, and then keeps showing the menu until the user chooses to quit.
     */
    private static void runQuickChat(Scanner scanner) {
        System.out.println("\n==============================================");
        System.out.println("Welcome to QuickChat.");
        System.out.println("==============================================");

        int messageLimit = readIntInRange(scanner,
                "\nHow many messages do you wish to enter? ", 1, Integer.MAX_VALUE);

        boolean messagesEntered = false;
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readIntInRange(scanner, "Choose an option (1-3): ", 1, 3);

            switch (choice) {
                case MENU_SEND_MESSAGES -> {
                    if (messagesEntered) {
                        System.out.println("\nYou have already entered all "
                                + messageLimit + " of your messages.");
                    } else {
                        sendMessages(scanner, messageLimit);
                        messagesEntered = true;
                    }
                }
                case MENU_RECENT_MESSAGES -> System.out.println("\nComing Soon.");
                case MENU_QUIT -> running = false;
                default -> System.out.println("\nInvalid option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1) Send Messages");
        System.out.println("2) Show recently sent messages");
        System.out.println("3) Quit");
    }

    /**
     * Lets the user enter exactly the number of messages they asked for. The
     * loop counter is used as the message nr, and the total is shown once
     * every message has been dealt with.
     */
    private static void sendMessages(Scanner scanner, int messageLimit) {
        for (int i = 0; i < messageLimit; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + messageLimit + " ---");

            String recipient = readRecipient(scanner);
            String text = readMessageText(scanner);

            Message message = new Message(i, recipient, text);
            System.out.println("\n" + message.getMessageIDStatus());

            processMessage(scanner, message);
        }

        System.out.println("\n--- MESSAGES SENT THIS SESSION ---");
        System.out.println(Message.printMessages());
        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    /** Keeps asking until the recipient's cell number is valid. */
    private static String readRecipient(Scanner scanner) {
        String recipient = null;

        while (recipient == null) {
            System.out.print("Enter the recipient's cell number (e.g. +27718693002): ");
            String input = scanner.nextLine().trim();

            String result = Message.checkRecipientCell(input);
            System.out.println(result);

            if (result.equals(Message.CELL_SUCCESS)) {
                recipient = input;
            }
        }

        return recipient;
    }

    /** Keeps asking until the message is not empty and not over 250 characters. */
    private static String readMessageText(Scanner scanner) {
        String text = null;

        while (text == null) {
            System.out.print("Enter your message (max 250 characters): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("A message cannot be empty.");
            } else {
                String result = Message.checkMessageLength(input);
                System.out.println(result);

                if (result.equals(Message.MESSAGE_READY)) {
                    text = input;
                }
            }
        }

        return text;
    }

    /** Asks whether to send, disregard or store the message, then acts on it. */
    private static void processMessage(Scanner scanner, Message message) {
        System.out.println("\nWhat would you like to do with this message?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");

        int option = readIntInRange(scanner, "Choose an option (1-3): ", 1, 3);
        System.out.println("\n" + message.sentMessage(option));

        if (option == Message.SEND_OPTION) {
            System.out.println("\n" + message.getMessageDetails());
        } else if (option == Message.DISREGARD_OPTION) {
            confirmDelete(scanner);
        }
    }

    /** Waits for the user to press 0 to delete a disregarded message. */
    private static void confirmDelete(Scanner scanner) {
        String input = "";

        while (!input.equals("0")) {
            System.out.print("Press 0: ");
            input = scanner.nextLine().trim();
        }

        System.out.println("Message deleted.");
    }

    /**
     * Asks for a whole number between min and max (inclusive) and keeps asking
     * until one is entered, so a wrong entry never crashes the program.
     */
    private static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        String range = (max == Integer.MAX_VALUE)
                ? "at least " + min
                : "between " + min + " and " + max;

        Integer value = null;

        while (value == null) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int parsed = Integer.parseInt(input);

                if (parsed >= min && parsed <= max) {
                    value = parsed;
                } else {
                    System.out.println("Please enter a number " + range + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }

        return value;
    }
}
