package edu.kit.kastel.game.model.status;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.decision.DecisionMaker;
import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Monster;
import edu.kit.kastel.game.model.ProtectTarget;
import edu.kit.kastel.game.model.StatusCondition;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.effect.DamageEffect;
import edu.kit.kastel.game.model.effect.Effect;
import edu.kit.kastel.game.model.effect.HittableEffect;
import edu.kit.kastel.game.model.effect.ProtectStatEffect;
import edu.kit.kastel.game.model.effect.StatusEffect;

/**
 * Represents the status of a monster in battle, including health, stats, effects, and status conditions. Handles applying actions and
 * calculating damage or healing.
 *
 * @author uupyx
 */
public final class MonsterStatus {

    private final Monster monster;
    private final DecisionMaker decisionMaker;

    private final StatManager statManager;
    private final EffectManager effectManager;
    private final StatusConditionManager statusConditionManager;
    private final DamageCalculator damageCalculator;

    private int health;

    private ProtectTarget protectionTarget;
    private int protectionRoundsLeft;

    /**
     * Constructs a MonsterStatus for a specific monster.
     *
     * @param monster The monster this status belongs to.
     * @param decisionMaker The decision maker for random decisions.
     */
    public MonsterStatus(Monster monster, DecisionMaker decisionMaker) {
        this.monster = monster;

        this.decisionMaker = decisionMaker;

        this.statManager = new StatManager(this);
        this.effectManager = new EffectManager(decisionMaker);
        this.statusConditionManager = new StatusConditionManager(this);
        this.damageCalculator = new DamageCalculator(this);

        this.health = monster.maxHealth();
    }

    /**
     * Checks if the monster has protection from certain effects.
     *
     * @return true if the monster is protected, false otherwise.
     */
    public boolean hasProtection() {
        return protectionTarget != null;
    }

    /**
     * Gets the protection target of the monster.
     *
     * @return The protection target.
     */
    public ProtectTarget getProtectionTarget() {
        return protectionTarget;
    }

    /**
     * Gets the remaining protection rounds.
     *
     * @return The number of protection rounds left.
     */
    public int getProtectionRoundsLeft() {
        return protectionRoundsLeft;
    }

    /**
     * Sets protection for the monster for a specified number of rounds.
     *
     * @param target The protection target.
     * @param rounds The number of rounds for the protection.
     */
    public void setProtection(ProtectTarget target, int rounds) {
        this.protectionTarget = target;
        this.protectionRoundsLeft = rounds;
    }
    
  /**
  * Decreases the remaining protection rounds and removes protection when the rounds reach zero.
  */
    public void decrementProtectionRounds() {
        if (protectionRoundsLeft > 0) {
            protectionRoundsLeft--;
            if (protectionRoundsLeft == 0) {
                Console.writeMessage(Message.MONSTER_PROTECTION_FADING, monster.name());
                protectionTarget = null;
            }
        }
    }

    /**
     * Applies damage to the monster and checks if it faints.
     *
     * @param target The target of the damage.
     * @param damage The amount of damage to apply.
     * @param burn true if the damage is burn damage, false otherwise.
     */
    public void damage(TargetMonster target, int damage, boolean burn) {
        if (damage <= 0d) {
            return;
        }

        if (hasProtection() && protectionTarget == ProtectTarget.HEALTH && !burn && target == TargetMonster.TARGET) {
            Console.writeMessage(Message.MONSTER_IS_PROTECTED_NO_DAMAGE, monster.name());
            return;
        }

        health = Math.max(0, health - damage);
        Console.writeMessage(burn ? Message.MONSTER_TAKES_DAMAGE_BURNING : Message.MONSTER_TAKES_DAMAGE, monster.name(),
                Math.round(damage));

        if (health <= 0) {
            statusConditionManager.changeStatusCondition(StatusCondition.FAINTED);
            Console.writeMessage(Message.MONSTER_FAINTS, monster.name());
        }
    }

    /**
     * Heals the monster by the specified amount.
     *
     * @param amount The amount of healing.
     */
    public void heal(int amount) {
        if (amount <= 0d) {
            return;
        }

        health = Math.min(monster.maxHealth(), health + amount);

        Console.writeMessage(Message.MONSTER_GAINS_HEALTH, monster.name(), amount);
    }

    /**
     * Performs the action for the monster, applying effects to the target.
     *
     * @param action The action to perform.
     * @param target The target monster of the action.
     */
    public void performAction(Action action, MonsterStatus target) {
        Queue<Effect> effectQueue = (action == null ? new ArrayDeque<>() : effectManager.buildEffectQueue(action));

        statusConditionManager.tryToEnd();
        
        if (statusConditionManager.getStatusCondition().preventsAction()) {
            effectQueue.clear(); // If asleep or something else, do nothing
        }

        if (action != null) {
            Console.writeMessage(Message.MONSTER_ACTION, monster.name(), action.name());
        } else {
            Console.writeMessage(Message.MONSTER_PASS, monster.name());
        }
        
        boolean alreadyPerformed = false;
        boolean isFirstEffect = true;
        while (!effectQueue.isEmpty()) {
            Effect effect = effectQueue.poll();

            if (effect instanceof HittableEffect hittable) {                
                boolean hit = effectManager.willEffectHit(hittable, statManager, target.getStatManager());
                if ((!hit && isFirstEffect)) {
                    Console.writeMessage(Message.ACTION_FAILED);
                    break;
                }
                if (!hit) {
                    continue;
                }

                if (effect instanceof ProtectStatEffect protectEffect && hit) {
                    int rounds = protectEffect.count().get(decisionMaker, "protection duration") + 1;
                    setProtection(protectEffect.target(), rounds);
                    Message message = (protectEffect.target() == ProtectTarget.HEALTH) ? Message.MONSTER_PROTECTED_AGAINST_DAMAGE
                            : Message.MONSTER_PROTECTED_AGAINST_STATUS;

                    Console.writeMessage(message, monster.name());
                }
            }

            if (effect instanceof StatusEffect statusEffect) {
                if (statusEffect.getTarget() == TargetMonster.TARGET && target.isFainted()) {
                    Console.writeMessage(Message.ACTION_FAILED);
                    break;
                }
                
                statusEffect.perform(action, this, target, alreadyPerformed);

                if (effect instanceof DamageEffect) {
                    alreadyPerformed = true;
                }
            }
            
            isFirstEffect = false;
        }

        statusConditionManager.getStatusCondition().applyEndOfTurnEffect(this);
    }

    /**
     * Checks whether the monster is fainted or not.
     *
     * @return Is fainted?
     */
    public boolean isFainted() {
        return getStatusConditionManager().getStatusCondition() == StatusCondition.FAINTED;
    }

    /**
     * Gets the monster associated with this status.
     *
     * @return The monster.
     */
    public Monster getMonster() {
        return monster;
    }

    /**
     * Gets the decision maker for random decisions.
     *
     * @return The decision maker.
     */
    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    /**
     * Gets the status condition manager for this monster.
     *
     * @return The status condition manager.
     */
    public StatusConditionManager getStatusConditionManager() {
        return statusConditionManager;
    }

    /**
     * Gets the stat manager for this monster.
     *
     * @return The stat manager.
     */
    public StatManager getStatManager() {
        return statManager;
    }

    /**
     * Gets the damage calculator for this monster.
     *
     * @return The damage calculator.
     */
    public DamageCalculator getDamageCalculator() {
        return damageCalculator;
    }

    /**
     * Gets the current health of the monster.
     *
     * @return The health.
     */
    public int getHealth() {
        return health;
    }
}
