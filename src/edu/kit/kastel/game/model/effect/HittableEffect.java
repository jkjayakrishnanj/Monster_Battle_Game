package edu.kit.kastel.game.model.effect;

/**
 * Represents an effect that can be applied to a target with a hit rate.
 *
 * @author uupyx
 */
public non-sealed interface HittableEffect extends Effect {

    /**
     * Gets the hit rate of the effect.
     *
     * @return the hit rate of the effect.
     */
    int getHitRate();

}
