package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Represents an effect that inflicts a stat change on a target.
 *
 * @author uupyx
 * @param target The target of the effect.
 * @param stat The stat to be changed.
 * @param change The amount to change the stat by.
 * @param hitRate The hit rate of the effect.
 */
public record InflictStatChangeEffect(TargetMonster target, Stat stat, int change, int hitRate) implements HittableEffect, StatusEffect {

    @Override
    public void perform(Action action, MonsterStatus user, MonsterStatus target, boolean performed) {
        target().get(user, target).getStatManager().changeStat(stat, change, target());
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
