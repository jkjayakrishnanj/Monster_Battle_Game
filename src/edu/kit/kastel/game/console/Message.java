package edu.kit.kastel.game.console;

/**
 * Enum representing various messages in the game.
 *
 * @author uupyx
 */
public enum Message {

    /** Configuration loaded message. */
    CONFIG_LOADED(true, "Loaded %d actions, %d monsters."),

    /** Competition start message. */
    COMPETITION_START(false, "The %d monsters enter the competition!"),

    /** Prompt for action. */
    WHAT_ACTION(true, "What should %s do?"),

    /** Indicates whose turn it is. */
    MONSTERS_TURN(true, "It's %s's turn."),

    /** Message for when a monster passes. */
    MONSTER_PASS(false, "%s passes!"),

    /** Message for when a monster uses an action. */
    MONSTER_ACTION(false, "%s uses %s!"),

    /** Indicates effectiveness of action. */
    VERY_EFFECTIVE(false, "It is very effective!"), NOT_VERY_EFFECTIVE(false, "It is not very effective..."),

    /** Critical hit message. */
    CRITICAL_HIT(false, "Critical hit!"),

    /** Action failed message. */
    ACTION_FAILED(false, "The action failed..."),

    /** Message for damage taken by a monster. */
    MONSTER_TAKES_DAMAGE(false, "%s takes %s damage!"),

    /** Message for health gained by a monster. */
    MONSTER_GAINS_HEALTH(false, "%s gains back %d health!"),

    /** Message for monster falling asleep. */
    MONSTER_FALLS_ASLEEP(false, "%s falls asleep!"), MONSTER_IS_ASLEEP(false, "%s is asleep!"), MONSTER_WOKE_UP(false, "%s woke up!"),

    /** Message for monster becoming wet. */
    MONSTER_BECOMES_WET(false, "%s becomes soaking wet!"), MONSTER_IS_WET(false, "%s is soaking wet!"),
    /** Message for monster becoming dry. */
    MONSTER_DRIED(false, "%s dried up!"),

    /** Message for monster catching fire. */
    MONSTER_CAUGHT_FIRE(false, "%s caught on fire!"), MONSTER_BURNING(false, "%s is burning!"),
    /** Message for monster taking damage from fire. */
    MONSTER_TAKES_DAMAGE_BURNING(false, "%s takes %d damage from burning!"), MONSTER_BURNING_FADED(false, "%s's burning has faded!"),

    /** Message for monster caught in quicksand. */
    MONSTER_CAUGHT_QUICKSAND(false, "%s gets caught by quicksand!"), MONSTER_IS_QUICKSAND(false, "%s is caught in quicksand!"),
    /** Message for monster escapes quicksand. */
    MONSTER_ESCAPED_QUICKSAND(false, "%s escaped the quicksand!"),

    /** Message for monster stats change. */
    MONSTER_STAT_RISES(false, "%s's %s rises!"), MONSTER_STAT_DECREASES(false, "%s's %s decreases..."),

    /** Message for monster protection. */
    MONSTER_PROTECTED_AGAINST_DAMAGE(false, "%s is now protected against damage!"),
    /** Message for monster protection. */
    MONSTER_PROTECTED_AGAINST_STATUS(false, "%s is now protected against status changes!"),
    /** Message for monster protection. */
    MONSTER_IS_PROTECTED_NO_DAMAGE(false, "%s is protected and takes no damage!"),
    /** Message for monster protection. */
    MONSTER_IS_PROTECTED_UNAFFECTED(false, "%s is protected and is unaffected!"),
    /** Message for monster protection. */
    MONSTER_PROTECTION_FADING(false, "%s's protection fades away..."),

    /** Message for monster fainting. */
    MONSTER_FAINTS(false, "%s faints!"),

    /** Message for the competition result. */
    MONSTER_WIN(true, "%s has no opponents left and wins the competition!"),
    /** Message for the competition result. */
    MONSTER_DRAW(true, "All monsters have fainted. The competition ends without a winner!"),

    /** Decision-making prompt. */
    DECIDE_YES_NO(false, "Decide %s: yes or no? (y/n)"),
    /** Decision-making prompt. */
    DECIDE_DOUBLE(false, "Decide %s: a number between %.2f and %.2f?"),
    /** Decision-making prompt. */
    DECIDE_INTEGER(false, "Decide %s: an integer between %d and %d?"),

    /** Stats display message. */
    SHOW_STATS(false, "STATS OF %s\nHP %d/%d, ATK %s, DEF %s, SPD %s, PRC %s, AGL %s"),

    /** Error message. */
    ERROR_MISSING_CONFIG(false, "Error, missing config file path."),
    /** Error message. */
    ERROR_CONFIG(false, "Error, failed to load config file."),
    /** Error message. */
    ERROR_MONSTER_EXIST(false, "Error, monster %s does not exist."),

    /** Error message. */
    ERROR(false, "Error, %s."),

    /** Error message. */
    ERROR_DEBUG_Y_N(false, "Error, enter y or n."),
    /** Error message. */
    ERROR_DEBUG_OUT_OF_RANGE(false, "Error, out of range."),
    /** Error message. */
    ERROR_DEBUG_INVALID_NUMBER(false, "Error, invalid number."),

    /** Error message. */
    ERROR_DOES_NOT_KNOW_ACTION(false, "Error, %s does not know the action %s."),
    /** Error message. */
    ERROR_NOT_VALID_OPPONENT(false, "Error, %s is not a valid opponent."),
    /** Error message. */
    ERROR_SPECIFY_TARGET(false, "Error, multiple opponents available, specify a target."),

    /** Error message. */
    ERROR_UNKNOWN_COMMAND(false, "Error, unknown command."),

    /** Action-related message. */
    ACTIONS_OF_MONSTER(false, "ACTIONS OF %s"),
    /** Action-related message. */
    ACTION_DETAIL(false, "%s: ELEMENT %s, Damage %s, HitRate %d"),
    /** Wrong arguments error. */
    ERROR_WRONG_ARGS(false, "Error, wrong arguments."),
    /** No competition running error. */
    ERROR_NO_COMPETITION(false, "Error, no competition running.");

    private final boolean extraNewLine;
    private final String format;

    Message(boolean extraNewLine, String format) {
        this.extraNewLine = extraNewLine;
        this.format = format;
    }

    /**
     * Returns the formatted message.
     *
     * @param args Arguments for formatting the message.
     * @return The formatted message.
     */
    public String getFormatted(Object... args) {
        return (extraNewLine ? "\n" : "") + String.format(format, args);
    }
}
