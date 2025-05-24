package edu.kit.kastel.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Lexical analyzer for configuration files (Spec A.4.1). Tokenizes input into meaningful units for parsing.
 *
 * @author uupyx
 */
public final class Lexer {

    // Keywords mapped to TokenTypes (Spec A.4.1, Tokens)
    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(Map.entry("action", TokenType.ACTION),
            Map.entry("end", TokenType.END), Map.entry("monster", TokenType.MONSTER), Map.entry("damage", TokenType.DAMAGE),
            Map.entry("inflictStatusCondition", TokenType.INFLICT_STATUS_CONDITION),
            Map.entry("inflictStatChange", TokenType.INFLICT_STAT_CHANGE), Map.entry("protectStat", TokenType.PROTECT_STAT),
            Map.entry("heal", TokenType.HEAL), Map.entry("repeat", TokenType.REPEAT), Map.entry("continue", TokenType.CONTINUE),
            Map.entry("base", TokenType.BASE), Map.entry("rel", TokenType.REL), Map.entry("abs", TokenType.ABS),
            Map.entry("random", TokenType.RANDOM), Map.entry("WATER", TokenType.WATER), Map.entry("FIRE", TokenType.FIRE),
            Map.entry("EARTH", TokenType.EARTH), Map.entry("NORMAL", TokenType.NORMAL), Map.entry("WET", TokenType.WET),
            Map.entry("BURN", TokenType.BURN), Map.entry("QUICKSAND", TokenType.QUICKSAND), Map.entry("SLEEP", TokenType.SLEEP),
            Map.entry("ATK", TokenType.ATK), Map.entry("DEF", TokenType.DEF), Map.entry("SPD", TokenType.SPD),
            Map.entry("PRC", TokenType.PRC), Map.entry("AGL", TokenType.AGL), Map.entry("health", TokenType.HEALTH),
            Map.entry("stats", TokenType.STATS), Map.entry("user", TokenType.USER), Map.entry("target", TokenType.TARGET));

    private final String input;
    private final int length;
    private int pos = 0;
    private int line = 0;
    private int column = 1;

    /**
     * Creates a lexer for the given input.
     *
     * @param input Configuration file content
     */
    public Lexer(String input) {
        this.input = input;
        this.length = input.length();
    }

    /**
     * Tokenizes input into tokens (Spec A.4.1).
     *
     * @return Unmodifiable list of tokens
     * @throws PositionException On unknown characters
     */
    public List<Token> tokenize() throws PositionException {
        List<Token> tokens = new LinkedList<>();

        while (!isAtEnd()) {
            skipWhitespaceExceptNewline();
            if (isAtEnd()) {
                break;
            }

            char c = peek();
            if (isNewLine(c)) {
                tokens.add(new Token(TokenType.NEWLINE, consumeNewline(), line, column));
            } else if (isNumberStart(c)) {
                tokens.add(numberToken());
            } else if (Character.isAlphabetic(c)) {
                tokens.add(identifierOrKeywordToken());
            } else {
                throw new PositionException("Unknown character", line, column);
            }
        }

        // Add end-of-file (EOF) token to indicate the end of input.
        tokens.add(new Token(TokenType.EOF, null, line, column));

        return Collections.unmodifiableList(new ArrayList<>(tokens));
    }

    private boolean isNewLine(char c) {
        return c == '\n' || c == '\r';
    }

    private boolean isNumberStart(char c) {
        return Character.isDigit(c) || ((c == '+' || c == '-') && (pos + 1 < length && Character.isDigit(input.charAt(pos + 1))));
    }

    private Token numberToken() {
        int startColumn = column;
        int startPos = pos;

        char c = peek();

        if (c == '+' || c == '-') {
            advance();
        }

        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
        }

        String lexeme = input.substring(startPos, pos);

        return new Token(TokenType.INTEGER, lexeme, line, startColumn);
    }

    private Token identifierOrKeywordToken() {
        int startColumn = column;
        int startPos = pos;

        while (!isAtEnd() && !Character.isWhitespace(peek())) {
            advance();
        }

        String lexeme = input.substring(startPos, pos);
        TokenType type = KEYWORDS.getOrDefault(lexeme, TokenType.IDENTIFIER);

        return new Token(type, lexeme, line, startColumn);
    }

    private void skipWhitespaceExceptNewline() {
        while (!isAtEnd()) {
            char c = peek();

            if (c == ' ' || c == '\t') {
                advance();
            } else {
                break;
            }
        }
    }

    private String consumeNewline() {
        StringBuilder stringBuilder = new StringBuilder(2);

        if (peek() == '\r') {
            stringBuilder.append(advance());

            if (!isAtEnd() && peek() == '\n') {
                stringBuilder.append(advance());
            }
        } else if (peek() == '\n') {
            stringBuilder.append(advance());
        }

        line++;
        column = 1;
        return stringBuilder.toString();
    }

    private char peek() {
        return input.charAt(pos);
    }

    private char advance() {
        char c = input.charAt(pos++);
        column++;
        return c;
    }

    private boolean isAtEnd() {
        return pos >= length;
    }
}
