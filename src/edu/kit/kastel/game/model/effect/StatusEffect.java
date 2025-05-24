package edu.kit.kastel.game.model.effect;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Represents an effect that modifies a monster's status.
 *
 * @author uupyx
 */
public sealed interface StatusEffect extends Effect
        permits DamageEffect, InflictStatusConditionEffect, InflictStatChangeEffect, HealEffect {

    /**
     * Performs the status effect on the target.
     *
     * @param action The action that triggered the effect.
     * @param user The monster performing the effect.
     * @param target The target of the effect.
     * @param alreadyPerformed Indicates if the effect has already been applied.
     */
    void perform(Action action, MonsterStatus user, MonsterStatus target, boolean alreadyPerformed);

    /**
     * Returns the target of the effect (user or target).
     *
     * @return The target of the effect.
     */
    TargetMonster getTarget();

}
