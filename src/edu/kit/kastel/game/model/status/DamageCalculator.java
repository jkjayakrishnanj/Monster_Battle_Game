package edu.kit.kastel.game.model.status;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Effectiveness;
import edu.kit.kastel.game.model.Element;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.strength.AbsStrength;
import edu.kit.kastel.game.model.strength.BaseStrength;
import edu.kit.kastel.game.model.strength.RelStrength;
import edu.kit.kastel.game.model.strength.Strength;

/**
 * Calculates the damage dealt by a monster's action based on various factors.
 *
 * @author uupyx
 */
public final class DamageCalculator {

    private final MonsterStatus monsterStatus;

    /**
     * Constructs a DamageCalculator for a specific monster.
     *
     * @param monsterStatus The status of the monster performing the action.
     */
    public DamageCalculator(MonsterStatus monsterStatus) {
        this.monsterStatus = monsterStatus;
    }

    /**
     * Calculates the burn damage based on the monster's max health.
     *
     * @return The calculated burn damage.
     */
    public int calculateBurnDamage() {
        return (int) Math.ceil((monsterStatus.getMonster().maxHealth()) * 0.1);
    }

    /**
     * Calculates the damage dealt by an action.
     *
     * @param action The action being performed.
     * @param strength The strength associated with the action.
     * @param user The user performing the action.
     * @param shouldPrint Whether to print effectiveness messages.
     * @param isDamage Whether the action is dealing damage.
     * @return The calculated damage value.
     */
    public int calculateDamage(Action action, Strength strength, MonsterStatus user, boolean shouldPrint, boolean isDamage) {
        double damage = 0;
        
        if (shouldPrint) {
            Console.writeMessage(action.element().getEffectiveness(monsterStatus.getMonster().element()).getMessage());
        }

        if (strength instanceof BaseStrength baseStrength) {
            double baseValue = baseStrength.value();
            Element actionElement = action.element();

            // Element factor
            Effectiveness effectiveness = actionElement.getEffectiveness(monsterStatus.getMonster().element());
            double elementsFactor = effectiveness.getMultiplier();

            // Stat factor
            double statFactor = user.getStatManager().getEffectiveStat(Stat.ATK)
                    / monsterStatus.getStatManager().getEffectiveStat(Stat.DEF);

            // Critical factor
            double spdUser = user.getStatManager().getEffectiveStat(Stat.SPD);
            double spdTarget = monsterStatus.getStatManager().getEffectiveStat(Stat.SPD);

            double critProbability = Math.pow(10, -spdTarget / spdUser) * 100;
//            boolean isCrit = isDamage && monsterStatus.getDecisionMaker().decideYesNo(critProbability, "critical hit");
            boolean isCrit = monsterStatus.getDecisionMaker().decideYesNo(critProbability, "critical hit");

            double critFactor = isCrit ? 2.0 : 1.0;
            if (isCrit) {
                Console.writeMessage(Message.CRITICAL_HIT);
            }

            // Same element factor
            double sameElementFactor = (actionElement == user.getMonster().element()) ? 1.5 : 1.0;

            // Random factor
            double randomFactor = monsterStatus.getDecisionMaker().decideDouble(0.85, 1.0, "damage variance");

            // Normalization factor
            double normalizationFactor = 1.0 / 3.0;

            damage = baseValue * elementsFactor * statFactor * critFactor * sameElementFactor * randomFactor * normalizationFactor;

            if (isDamage) {
                damage = Math.ceil(damage);
            }
        } else if (strength instanceof RelStrength relStrength) {
            damage = ((relStrength.percentage()) / 100.0) * (monsterStatus.getMonster().maxHealth());
        } else if (strength instanceof AbsStrength absStrength) {
            damage = absStrength.value();
        }

        return (int) Math.round(damage);
    }

}
