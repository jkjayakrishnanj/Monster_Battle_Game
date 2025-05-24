package edu.kit.kastel.game.model.count;

import edu.kit.kastel.game.decision.DecisionMaker;

/**
 * Represents a random count value for repeated effects.
 * <p>
 * This type of count is used when an effect has a randomized number of repetitions. The number is determined within the given range (min,
 * max).
 * </p>
 *
 * @param min The minimum number of repetitions.
 * @param max The maximum number of repetitions.
 *
 * @author uupyx
 */
public record RandomCount(int min, int max) implements Count {

    @Override
    public int get(DecisionMaker decisionMaker, String context) {
        return decisionMaker.decideInt(min, max, context);
    }
}