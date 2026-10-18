/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.poe.part2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Message class, using the test data from the PoE.
 */
class MessageTest {

    // Test data. Test case 1 is the "dinner tonight" message; test case 2 is
    // the message from the "Test Data for Message 2" table.
    private static final String TEST1_RECIPIENT = "+27718693002";
    private static final String TEST1_TEXT = "Hi Mike, can you join us for dinner tonight";
    private static final String TEST2_RECIPIENT = "08575975889";
    private static final String TEST2_TEXT = "Hi Keegan, did you receive the payment?";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Message.clearSentMessages();
        Message.setStorageFile(tempDir.resolve("messages.json"));
    }

    // -----------------------------------------------------------------
    // Message length
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Message of 250 characters or fewer is accepted")
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(TEST1_TEXT));
        assertEquals("Message ready to send.", Message.checkMessageLength("a".repeat(250)));
    }

    @Test
    @DisplayName("Message over 250 characters is rejected with the excess shown")
    void messageLengthFailure() {
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength("a".repeat(260)));
    }

    // -----------------------------------------------------------------
    // Recipient number
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Recipient number is correctly formatted")
    void recipientCorrectlyFormatted() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell(TEST1_RECIPIENT));
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27838968976"));
    }

    @Test
    @DisplayName("Recipient number is incorrectly formatted")
    void recipientIncorrectlyFormatted() {
        String expected = "Cell phone number is incorrectly formatted or does not "
                + "contain an international code. Please correct the number and try again.";

        assertEquals(expected, Message.checkRecipientCell(TEST2_RECIPIENT));
        assertEquals(expected, Message.checkRecipientCell("08966553"));
    }

    // -----------------------------------------------------------------
    // Message hash
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Message hash is correct for test case 1")
    void messageHashTestCase1() {
        Message message = new Message("0012345678", 0, TEST1_RECIPIENT, TEST1_TEXT);

        assertEquals("00:0:HITONIGHT", message.createMessageHash());
    }

    @Test
    @DisplayName("Message hashes are correct for the remaining test data (loop)")
    void messageHashesInLoop() {
        String[] ids = {"0012345678", "0098765432", "0012345678", "9876543210"};
        int[] numbers = {0, 1, 0, 2};
        String[] texts = {TEST1_TEXT, TEST2_TEXT, "Hi, thanks",
                "Okay, I am leaving without you."};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT", "00:0:HITHANKS",
                "98:2:OKAYYOU"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], numbers[i], TEST1_RECIPIENT, texts[i]);

            assertEquals(expected[i], message.createMessageHash());
            assertEquals(expected[i], message.getMessageHash());
        }
    }

    // -----------------------------------------------------------------
    // Message ID
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Message ID is created")
    void messageIdCreated() {
        Message message = new Message(0, TEST1_RECIPIENT, TEST1_TEXT);

        assertTrue(message.checkMessageID());
        assertTrue(message.getMessageId().matches("\\d{10}"));
        assertEquals("Message ID generated: " + message.getMessageId(),
                message.getMessageIDStatus());
    }

    @Test
    @DisplayName("Message ID longer than ten characters is rejected")
    void messageIdTooLong() {
        Message message = new Message("12345678901", 0, TEST1_RECIPIENT, TEST1_TEXT);

        assertFalse(message.checkMessageID());
    }

    // -----------------------------------------------------------------
    // Send, disregard or store
    // -----------------------------------------------------------------

    @Test
    @DisplayName("User selected 'Send Message'")
    void sendMessage() {
        Message message = new Message(0, TEST1_RECIPIENT, TEST1_TEXT);

        assertEquals("Message successfully sent.", message.sentMessage(1));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("User selected 'Disregard Message'")
    void disregardMessage() {
        Message message = new Message(1, TEST2_RECIPIENT, TEST2_TEXT);

        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("User selected 'Store Message'")
    void storeMessage() throws IOException {
        Message message = new Message("0012345678", 0, TEST1_RECIPIENT, TEST1_TEXT);

        assertEquals("Message successfully stored.", message.sentMessage(3));
        assertEquals(0, Message.returnTotalMessages());

        Path file = tempDir.resolve("messages.json");
        assertTrue(Files.exists(file));

        String json = Files.readString(file);
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("00:0:HITONIGHT"));
        assertTrue(json.contains(TEST1_RECIPIENT));
        assertTrue(json.contains(TEST1_TEXT));
    }

    @Test
    @DisplayName("Storing a second message keeps the first in the JSON file")
    void storeTwoMessages() throws IOException {
        new Message("0012345678", 0, TEST1_RECIPIENT, TEST1_TEXT).sentMessage(3);
        new Message("0098765432", 1, TEST1_RECIPIENT, TEST2_TEXT).sentMessage(3);

        String json = Files.readString(tempDir.resolve("messages.json"));
        assertTrue(json.contains("0012345678"));
        assertTrue(json.contains("0098765432"));
    }

    @Test
    @DisplayName("An invalid option is reported")
    void invalidOption() {
        Message message = new Message(0, TEST1_RECIPIENT, TEST1_TEXT);

        assertEquals("Invalid option; please choose 1, 2 or 3.", message.sentMessage(9));
    }

    // -----------------------------------------------------------------
    // Totals and printing
    // -----------------------------------------------------------------

    @Test
    @DisplayName("Total only counts the messages that were sent")
    void totalMessagesSent() {
        Message first = new Message("0012345678", 0, TEST1_RECIPIENT, TEST1_TEXT);
        Message second = new Message("0098765432", 1, TEST2_RECIPIENT, TEST2_TEXT);

        first.sentMessage(1);
        second.sentMessage(2);

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    @DisplayName("Sent messages are printed in the required order")
    void printMessagesOrder() {
        Message message = new Message("0012345678", 0, TEST1_RECIPIENT, TEST1_TEXT);
        message.sentMessage(1);

        String expected = String.join(System.lineSeparator(),
                "Message ID: 0012345678",
                "Message Hash: 00:0:HITONIGHT",
                "Recipient: +27718693002",
                "Message: Hi Mike, can you join us for dinner tonight");

        assertEquals(expected, Message.printMessages());
    }

    @Test
    @DisplayName("Printing with no sent messages")
    void printMessagesWhenNoneSent() {
        assertEquals("No messages have been sent.", Message.printMessages());
    }
}