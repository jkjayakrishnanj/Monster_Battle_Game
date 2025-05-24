package edu.kit.kastel.game.decision;

/**
 * Interface for decision-making strategies.
 *
 * @author uupyx
 */
public interface DecisionMaker {

    /**
     * Decides a yes/no decision based on the given probability and context.
     *
     * @param probability The probability for the decision (0 - 100).
     * @param context The context of the decision.
     * @return True if 'yes', false if 'no'.
     */
    boolean decideYesNo(double probability, String context);

    /**
     * Decides a double value within a specified range based on the context.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @param context The context of the decision.
     * @return The decided double value.
     */
    double decideDouble(double min, double max, String context);

    /**
     * Decides an integer value within a specified range based on the context.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @param context The context of the decision.
     * @return The decided integer value.
     */
    int decideInt(int min, int max, String context);
}
