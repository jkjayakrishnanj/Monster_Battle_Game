package edu.kit.kastel.game.model;

import java.util.List;

import edu.kit.kastel.game.model.effect.Effect;

/**
 * Represents an action that a monster can perform in battle.
 *
 * Each action has a name, an associated element, and a list of effects, that define its behaviour. Actions are defined in the configuration
 * file and used during the battle.
 *
 * @param name The name of the action.
 * @param element The element associated with the action (WATER, FIRE, EARTH, NORMAL).
 * @param effects The list of effects this action applies.
 *
 * @author uupyx
 */
public record Action(String name, Element element, List<Effect> effects) {

}
