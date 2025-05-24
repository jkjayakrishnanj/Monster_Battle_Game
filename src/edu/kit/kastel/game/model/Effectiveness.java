package edu.kit.kastel.game.model;

import edu.kit.kastel.game.console.Message;

/**
 * Represents the effectiveness of attacks between elements. The effectiveness determines how effective an attack is depending on the
 * attacker's and defender's elemental type.
 *
 * @author uupyx
 */
public enum Effectiveness {

    /**
     * Very effective.
     */
    VERY(2.0, Message.VERY_EFFECTIVE),

    /**
     * Not very effective.
     */
    NOT_VERY(0.5, Message.NOT_VERY_EFFECTIVE),

    /**
     * Normal.
     */
    NORMAL(1.0, null);

    private final double multiplier;
    private final Message message;

    Effectiveness(double multiplier, Message message) {
        this.multiplier = multiplier;
        this.message = message;
    }

    /**
     * Get the effectiveness multiplier, for later calculations.
     *
     * @return The multiplier.
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Get the message, which shows how effective it is.
     *
     * @return The message, or null, if none.
     */
    public Message getMessage() {
        return message;
    }

}
