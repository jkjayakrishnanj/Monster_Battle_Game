package edu.kit.kastel.game.model.strength;

/**
 * Represents a base value of strength.
 *
 * @param value The value.
 * @author uupyx
 */
public record BaseStrength(int value) implements Strength {

    @Override
    public String toString() {
        return "b" + value;
    }

}
