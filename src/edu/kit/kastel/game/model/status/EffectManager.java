package edu.kit.kastel.game.model.status;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.kit.kastel.game.decision.DecisionMaker;
import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.effect.Effect;
import edu.kit.kastel.game.model.effect.HittableEffect;
import edu.kit.kastel.game.model.effect.RepeatEffect;
import edu.kit.kastel.game.model.effect.StatusEffect;

/**
 * Manages the effects of actions during a battle. Handles building effect queues and determining the likelihood of effects hitting.
 *
 * @author uupyx
 */
public class EffectManager {

    private final DecisionMaker decisionMaker;

    /**
     * Constructs an EffectManager with a specified decision maker.
     *
     * @param decisionMaker The decision maker for random decisions.
     */
    public EffectManager(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    /**
     * Builds a queue of effects to be applied from an action's effects. Handles repeated effects by adding them multiple times to the
     * queue.
     *
     * @param action The action whose effects need to be processed.
     * @return A queue containing all the effects to be applied.
     */
    public Queue<Effect> buildEffectQueue(Action action) {
        Queue<Effect> queue = new ArrayDeque<>();
        
        for (Effect effect : action.effects()) {
            if (effect instanceof RepeatEffect repeat) {
                int repeatCount = repeat.count().get(decisionMaker, "repeat count");
                
                for (int i = 0; i < repeatCount; i++) {
                    queue.addAll(repeat.effects());
                }
            } else {
                queue.add(effect);
            }
        }

        return queue;
    }

    /**
     * Determines whether a hittable effect will hit based on hit rate, status effects, and stats.
     *
     * @param effect The hittable effect to check.
     * @param user The stat manager of the user performing the action.
     * @param target The stat manager of the target being affected.
     * @return true if the effect will hit, false otherwise.
     */
    public boolean willEffectHit(HittableEffect effect, StatManager user, StatManager target) {
        double baseHitRate = effect.getHitRate();
        double statusQuotient = 1.0;

        final double userPRC = user.getEffectiveStat(Stat.PRC);

        if (effect instanceof StatusEffect statusEffect) {
            if (statusEffect.getTarget() == TargetMonster.TARGET) {
                statusQuotient = userPRC / target.getEffectiveStat(Stat.AGL);
            } else {
                statusQuotient = userPRC;
            }
        }

        double finalProbability = baseHitRate * statusQuotient;

        return decisionMaker.decideYesNo(finalProbability, "effect hit");
    }
}
