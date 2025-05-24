package edu.kit.kastel.game.model.strength;

/**
 * Represents a fixed value of strength.
 *
 * @param value The value.
 * @author uupyx
 */
public record AbsStrength(int value) implements Strength {

    @Override
    public String toString() {
        return "a" + value;
    }

}
