package edu.kit.kastel.game.model.count;

import edu.kit.kastel.game.decision.DecisionMaker;

/**
 * Represents a fixed count value for repeated effects.
 * <p>
 * This type of count is used when an effect has a predetermined number of repetitions.
 * </p>
 *
 * @param value The fixed number of repetitions.
 *
 * @author uupyx
 */
public record FixedCount(int value) implements Count {

    @Override
    public int get(DecisionMaker decisionMaker, String context) {
        return value;
    }
}
