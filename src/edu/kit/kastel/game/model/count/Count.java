package edu.kit.kastel.game.model.count;

import edu.kit.kastel.game.decision.DecisionMaker;

/**
 * Represents a count value for repeated effects.
 * <p>
 * Some effects, such as multi-hit attacks, use count values to determine how many times they occur. A count can be either a fixed value or
 * randomly determined (see Spec A.2.4 for repetition mechanics).
 * </p>
 *
 * @author uupyx
 */
public sealed interface Count permits FixedCount, RandomCount {

    /**
     * Gets the count value.
     *
     * @param decisionMaker A decision maker.
     * @param context The context to be provided.
     * @return The count value (number of repetitions) for the effect.
     */
    int get(DecisionMaker decisionMaker, String context);
}
