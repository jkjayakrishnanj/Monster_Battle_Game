package edu.kit.kastel.game.console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Manages console input and output for the game.
 *
 * @author uupyx
 */
public final class Console implements AutoCloseable {

    private static Console console;

    private final BufferedReader reader;
    private final BufferedWriter writer;

    /**
     * Private constructor to set up the console's input and output streams.
     */
    private Console() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        writer = new BufferedWriter(new OutputStreamWriter(System.out));
    }

    /**
     * Initializes the console for input and output operations.
     */
    public static void initialize() {
        if (console != null) {
            return;
        }

        console = new Console();
    }

    /**
     * Terminates the console and closes the resources.
     */
    public static void terminate() {
        Console.console.close();
    }

    /**
     * Reads a line of text from the console.
     *
     * @return The line read from the console, or an empty string if an error occurs.
     */
    public static String readLine() {
        try {
            return console.reader.readLine();
        } catch (IOException e) {

            return "";
        }
    }

    /**
     * Writes a string to the console without adding a new line.
     *
     * @param content The content to write.
     */
    public static void writeVerbatim(String content) {
        try {
            console.writer.write(content);
            console.writer.flush();
        } catch (IOException e) {
            // Handle if needed, outside the control of the programmer
        }
    }

    /**
     * Writes a line of text to the console with a new line after it.
     *
     * @param line The line to write.
     */
    public static void writeLine(String line) {
        try {
            console.writer.write(line);
            console.writer.newLine();
            console.writer.flush();
        } catch (IOException e) {
            // Handle if needed, outside the control of the programmer
        }
    }

    /**
     * Writes a formatted message to the console.
     *
     * @param message The message to display.
     * @param args Arguments to format the message.
     */
    public static void writeMessage(Message message, Object... args) {
        if (message == null) {
            return;
        }

        writeLine(message.getFormatted(args));
    }

    /**
     * Closes the console's input and output streams.
     */
    @Override
    public void close() {
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            // Handle if needed, outside the control of the programmer
        }
    }

}
