package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.ProtectTarget;
import edu.kit.kastel.game.model.count.Count;

/**
 * Represents an effect that protects a monster's stat from changes.
 *
 * @author uupyx
 * @param target The target to be protected.
 * @param count The count of protection (e.g., how many turns it lasts).
 * @param hitRate The hit rate of the effect.
 */
public record ProtectStatEffect(ProtectTarget target, Count count, int hitRate) implements HittableEffect {

    @Override
    public int getHitRate() {
        return hitRate;
    }
}
