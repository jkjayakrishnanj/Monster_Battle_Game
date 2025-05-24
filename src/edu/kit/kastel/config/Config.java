package edu.kit.kastel.config;

import java.util.Map;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Monster;

/**
 * Holds the game configuration parsed from a file. Stores actions and monsters for battle initialization.
 *
 * @param actions Maps action names to Action objects.
 * @param monsters Maps monster names to Monster objects.
 *
 * @author uupyx
 */
public record Config(Map<String, Action> actions, Map<String, Monster> monsters) {
}
