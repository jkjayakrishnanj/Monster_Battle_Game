package edu.kit.kastel.config;

/**
 * Represents a single token in the lexical analysis of the monster battle config.
 *
 * @param type The type
 * @param lexeme The actual string representation of the token from the input.
 * @param line The line number where the token appears in the input.
 * @param column The column number where the token starts in the input.
 *
 * @author uupyx
 */
public record Token(TokenType type, String lexeme, int line, int column) {

}
