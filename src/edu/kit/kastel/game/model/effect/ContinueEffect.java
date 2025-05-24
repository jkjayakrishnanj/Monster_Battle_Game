package edu.kit.kastel.game.model.effect;

/**
 * Represents an effect that continues based on hit rate.
 *
 * @param hitRate The hit rate
 *
 * @author uupyx
 */
public record ContinueEffect(int hitRate) implements HittableEffect {

    @Override
    public int getHitRate() {
        return hitRate;
    }

}
