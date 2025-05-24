package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.status.MonsterStatus;
import edu.kit.kastel.game.model.strength.Strength;

/**
 * Represents a damage effect that applies to a target monster.
 *
 * @param targetMonster The target of the effect (user or opponent).
 * @param strength The strength of the effect's damage.
 * @param hitRate The chance for the effect to hit.
 * @param burn Whether the effect causes burn damage.
 *
 * @author uupyx
 */
public record DamageEffect(TargetMonster targetMonster, Strength strength, int hitRate, boolean burn)
        implements HittableEffect, StatusEffect {

    @Override
    public void perform(Action action, MonsterStatus user, MonsterStatus target, boolean alreadyPerformed) {
        MonsterStatus realTarget = getTarget().get(user, target);
        realTarget.damage(getTarget(), realTarget.getDamageCalculator().calculateDamage(action, strength, user, !alreadyPerformed, true),
                false);
    }

    @Override
    public int getHitRate() {
        return hitRate;
    }

    @Override
    public TargetMonster getTarget() {
        return targetMonster;
    }
}
