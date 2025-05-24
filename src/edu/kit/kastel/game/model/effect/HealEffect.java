package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.status.MonsterStatus;
import edu.kit.kastel.game.model.strength.Strength;

/**
 * Represents a healing effect applied to a target.
 *
 * @param target The target of the healing effect.
 * @param strength The strength of the healing effect.
 * @param hitRate The chance of the healing effect occurring.
 * @author uupyx
 */
public record HealEffect(TargetMonster target, Strength strength, int hitRate) implements HittableEffect, StatusEffect {

    @Override
    public void perform(Action action, MonsterStatus user, MonsterStatus target, boolean alreadyPerformed) {
        MonsterStatus realTarget = getTarget().get(user, target);
        realTarget.heal(realTarget.getDamageCalculator().calculateDamage(action, strength, user, !alreadyPerformed, false));
    }

    @Override
    public int getHitRate() {
        return hitRate;
    }

    @Override
    public TargetMonster getTarget() {
        return target;
    }

}
