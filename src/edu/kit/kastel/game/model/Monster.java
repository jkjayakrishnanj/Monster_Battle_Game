package edu.kit.kastel.game.model;

import java.util.List;

/**
 * Represents the immutable base attributes of a monster, as defined in the configuration. A monster can execute a predefined set of
 * actions.
 *
 * @see edu.kit.kastel.config.Parser
 *
 * @param id The unique competition ID.
 * @param name The name of the monster.
 * @param element The elemental type of the monster.
 * @param maxHealth The maximum health (HP) of the monster.
 * @param baseAttack The base attack value (ATK).
 * @param baseDefense The base defense value (DEF).
 * @param baseSpeed The base speed value (SPD).
 * @param actions The list of actions this monster can perform.
 *
 * @author uupyx
 */
public record Monster(int id, String name, Element element, int maxHealth, int baseAttack, int baseDefense, int baseSpeed,
        List<Action> actions) {

    @Override
    public String toString() {
        return String.format("%s: ELEMENT %s, HP %d, ATK %d, DEF %d, SPD %d\n", name, element().name(), maxHealth, baseAttack, baseDefense,
                baseSpeed);
    }

}
