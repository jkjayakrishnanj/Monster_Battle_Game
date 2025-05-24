package edu.kit.kastel.config;

/**
 * Exception thrown for errors at specific positions in the input. Provides the line and column where the error occurred.
 *
 * @author uupyx
 */
public final class PositionException extends Exception {

    private static final long serialVersionUID = 1L;

    /** The line number where the error occurred. */
    public final int line;

    /** The column number where the error occurred. */
    public final int column;

    /**
     * Constructs an exception with a message and position details.
     *
     * @param message A description of the error.
     * @param line The line number where the error occurred.
     * @param column The column number where the error occurred.
     */
    public PositionException(String message, int line, int column) {
        super("In line " + line + " at " + column + ": " + message);
        this.line = line;
        this.column = column;
    }
}
