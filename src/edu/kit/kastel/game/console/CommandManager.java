package edu.kit.kastel.game.console;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages command registration and execution. Parses user input and matches it to registered commands.
 *
 * @author uupyx
 */
public final class CommandManager {

    private final List<CommandEntry> commands;

    /**
     * Initializes an empty command manager.
     */
    public CommandManager() {
        commands = new ArrayList<>();
    }

    /**
     * Registers a command with a pattern.
     *
     * @param pattern The command pattern to match user input.
     * @param command The command to execute when matched.
     */
    public void register(CommandPattern pattern, Command command) {
        commands.add(new CommandEntry(pattern, command));
    }

    /**
     * Reads input, matches it against registered commands, and executes the corresponding command. If no command matches, an error message
     * is displayed.
     */
    public void parse() {
        String input = Console.readLine();
        String[] tokens = input.trim().split("\\s+");
        for (CommandEntry entry : commands) {
            String[] args = entry.pattern().match(tokens);
            if (args != null) {
                entry.command().execute(args);
                return;
            }
        }

        Console.writeMessage(Message.ERROR_UNKNOWN_COMMAND);
    }

    /**
     * Associates a command pattern with a command.
     */
    private record CommandEntry(CommandPattern pattern, Command command) {

    }

    /**
     * Defines a pattern for recognizing commands.
     */
    public interface CommandPattern {

        /**
         * Checks if the given tokens match the command pattern.
         *
         * @param tokens The user input tokens.
         * @return The extracted arguments if matched, otherwise null.
         */
        String[] match(String[] tokens);

    }

    /**
     * Represents a command that can be executed.
     */
    public interface Command {

        /**
         * Executes the command with the given arguments.
         *
         * @param args The parsed command arguments.
         */
        void execute(String[] args);

    }

    /**
     * Matches a command with a fixed sequence of tokens.
     *
     * @param tokens The expected command tokens.
     * @return A command pattern that matches only the exact sequence.
     */
    public static CommandPattern fixed(String... tokens) {
        return inputTokens -> fixed(tokens, inputTokens);
    }

    /**
     * Checks if the input tokens exactly match the expected tokens.
     *
     * @param tokens The expected command tokens.
     * @param inputTokens The user input tokens.
     * @return An empty argument array if matched, otherwise null.
     */
    private static String[] fixed(String[] tokens, String[] inputTokens) {
        if (inputTokens.length != tokens.length) {
            return null;
        }
        for (int i = 0; i < tokens.length; i++) {
            if (!inputTokens[i].equalsIgnoreCase(tokens[i])) {
                return null;
            }
        }
        return new String[0]; // No more commands
    }

    /**
     * Matches a command with a fixed number of arguments.
     *
     * @param commandName The command name.
     * @param expectedArgCount The required number of arguments.
     * @return A command pattern that matches when the exact number of arguments is provided.
     */
    public static CommandPattern fixedCount(String commandName, int expectedArgCount) {
        return inputTokens -> {
            if ((inputTokens.length != 1 + expectedArgCount) || !inputTokens[0].equalsIgnoreCase(commandName)) {
                return null;
            }
            String[] args = new String[expectedArgCount];
            System.arraycopy(inputTokens, 1, args, 0, expectedArgCount);
            return args;
        };
    }

    /**
     * Matches a command with a variable number of arguments, requiring at least a minimum count.
     *
     * @param commandName The command name.
     * @param minArgs The minimum number of required arguments.
     * @return A command pattern that allows additional arguments beyond the minimum.
     */
    public static CommandPattern variableCount(String commandName, int minArgs) {
        return inputTokens -> {
            if ((inputTokens.length < 1 + minArgs) || !inputTokens[0].equalsIgnoreCase(commandName)) {
                return null;
            }
            String[] args = new String[inputTokens.length - 1];
            System.arraycopy(inputTokens, 1, args, 0, args.length);
            return args;
        };
    }

    /**
     * Matches a command with required and optional arguments.
     *
     * @param commandName The command name.
     * @param requiredArgs The number of required arguments.
     * @param optionalArgs The number of optional arguments allowed.
     * @return A command pattern that matches when the number of arguments is within the required and optional range.
     */
    public static CommandPattern hybrid(String commandName, int requiredArgs, int optionalArgs) {
        return inputTokens -> {
            int totalArgs = inputTokens.length - 1; // without name
            if (totalArgs < requiredArgs || totalArgs > (requiredArgs + optionalArgs) || !inputTokens[0].equalsIgnoreCase(commandName)) {
                return null;
            }
            String[] args = new String[totalArgs];
            System.arraycopy(inputTokens, 1, args, 0, totalArgs);
            return args;
        };
    }

}
