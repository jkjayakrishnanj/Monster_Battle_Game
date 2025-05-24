package edu.kit.kastel.game.model.strength;

/**
 * Represents a strength value that can be one of BaseStrength, RelStrength, or AbsStrength.
 *
 * @author uupyx
 */
public sealed interface Strength permits BaseStrength, RelStrength, AbsStrength {
}
