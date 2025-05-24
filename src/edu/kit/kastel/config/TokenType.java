package edu.kit.kastel.config;

/**
 * Token types for the lexer.
 *
 * @author uupyx
 */
public enum TokenType {
    /** Action token. */
    ACTION,

    /** End token. */
    END,

    /** Monster token. */
    MONSTER,

    /** Damage token. */
    DAMAGE,

    /** Inflict status condition token. */
    INFLICT_STATUS_CONDITION,

    /** Inflict stat change token. */
    INFLICT_STAT_CHANGE,

    /** Protect stat token. */
    PROTECT_STAT,

    /** Heal token. */
    HEAL,

    /** Repeat token. */
    REPEAT,

    /** Continue token. */
    CONTINUE,

    /** Base token. */
    BASE,

    /** Relative token. */
    REL,

    /** Absolute token. */
    ABS,

    /** Random token. */
    RANDOM,

    /** Water token. */
    WATER,

    /** Fire token. */
    FIRE,

    /** Earth token. */
    EARTH,

    /** Normal token. */
    NORMAL,

    /** Wet token. */
    WET,

    /** Burn token. */
    BURN,

    /** Quicksand token. */
    QUICKSAND,

    /** Sleep token. */
    SLEEP,

    /** Attack token. */
    ATK,

    /** Defense token. */
    DEF,

    /** Speed token. */
    SPD,

    /** Precision token. */
    PRC,

    /** Agility token. */
    AGL,

    /** Health token. */
    HEALTH,

    /** Stats token. */
    STATS,

    /** User token. */
    USER,

    /** Target token. */
    TARGET,

    /** Identifier token. */
    IDENTIFIER,

    /** Integer token. */
    INTEGER,

    /** Newline token. */
    NEWLINE,

    /** End of file token. */
    EOF
}
