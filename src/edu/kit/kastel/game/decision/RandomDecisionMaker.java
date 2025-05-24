package edu.kit.kastel.game.decision;

import java.util.Random;

/**
 * Random decision maker that provides stochastic decision-making using a random number generator.
 *
 * @author uupyx
 */
public final class RandomDecisionMaker implements DecisionMaker {

    private final Random random;

    /**
     * Initializes the random decision maker with the given seed.
     *
     * @param seed The seed for the random number generator.
     */
    public RandomDecisionMaker(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public boolean decideYesNo(double probability, String context) {
        return random.nextDouble() * 100 <= probability;
    }

    @Override
    public double decideDouble(double min, double max, String context) {
        return random.nextDouble() * (max - min) + min;
    }

    @Override
    public int decideInt(int min, int max, String context) {
        return random.nextInt(max - min + 1) + min;
    }

}
