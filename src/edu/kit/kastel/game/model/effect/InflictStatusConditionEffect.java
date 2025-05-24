package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.StatusCondition;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Represents an effect that inflicts a status condition on a target.
 *
 * @author uupyx
 * @param target The target of the effect.
 * @param condition The status condition to be inflicted.
 * @param hitRate The hit rate of the effect.
 */
public record InflictStatusConditionEffect(TargetMonster target, StatusCondition condition, int hitRate)
        implements HittableEffect, StatusEffect {

    @Override
    public void perform(Action action, MonsterStatus user, MonsterStatus target, boolean performed) {
        getTarget().get(user, target).getStatusConditionManager().changeStatusCondition(condition);

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
