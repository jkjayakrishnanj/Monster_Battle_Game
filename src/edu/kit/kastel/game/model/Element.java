package edu.kit.kastel.game.model;

/**
 * Represents elemental types of monsters and actions. Elements determine the effectiveness of attacks against other elements. see Spec
 * A.2.6, A.2.8
 *
 * @author uupyx
 */
public enum Element {
    /**
     * Water.
     */
    WATER,
    /**
     * Fire.
     */
    FIRE,
    /**
     * Earth.
     */
    EARTH,
    /**
     * Normal.
     */
    NORMAL;

    /**
     * Determines the effectiveness of an attack from this element (the attacker) against another (the defender).
     *
     * @param defender The element of the defending monster.
     * @see Effectiveness
     * @return VERY if the attack is very effective, NOT_VERY if not very effective, and NORMAL otherwise.
     */
    public Effectiveness getEffectiveness(final Element defender) {
        final Element attacker = this;

        if (attacker == defender || attacker == NORMAL || defender == NORMAL) {
            return Effectiveness.NORMAL; // Normal effectiveness
        }

        return switch (attacker) {
            case WATER -> (defender == FIRE) ? Effectiveness.VERY : (defender == EARTH) ? Effectiveness.NOT_VERY : Effectiveness.NORMAL;
            case FIRE -> (defender == EARTH) ? Effectiveness.VERY : (defender == WATER) ? Effectiveness.NOT_VERY : Effectiveness.NORMAL;
            case EARTH -> (defender == WATER) ? Effectiveness.VERY : (defender == FIRE) ? Effectiveness.NOT_VERY : Effectiveness.NORMAL;
            default -> Effectiveness.NORMAL; // Should not happen
        };
    }
}
