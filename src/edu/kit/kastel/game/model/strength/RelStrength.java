package edu.kit.kastel.game.model.strength;

/**
 * Represents a relative value of strength.
 *
 * @param percentage The percentage.
 * @author uupyx
 */
public record RelStrength(int percentage) implements Strength {

    @Override
    public String toString() {
        return "r" + percentage;
    }
}
