package edu.kit.kastel.game.model.status;

import java.util.EnumMap;
import java.util.Map;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.model.ProtectTarget;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.TargetMonster;

/**
 * Manages the stats of a monster, including stat changes and the calculation of effective stats. Handles stat modifications and protection
 * against stat changes.
 *
 * @author uupyx
 */
public final class StatManager {

    private final MonsterStatus monsterStatus;
    private final Map<Stat, Integer> statChanges;

    /**
     * Constructs a StatManager for a specific monster.
     *
     * @param monsterStatus The monster status this manager is associated with.
     */
    public StatManager(MonsterStatus monsterStatus) {
        this.monsterStatus = monsterStatus;

        statChanges = new EnumMap<>(Stat.class);
    }

    /**
     * Gets the current value of a specific stat.
     *
     * @param stat The stat to retrieve the value for.
     * @return The current value of the stat.
     */
    public int getStat(Stat stat) {
        return statChanges.getOrDefault(stat, 0);
    }

    /**
     * Changes the value of a specific stat, respecting protection and stat limits.
     *
     * @param stat The stat to change.
     * @param change The amount to change the stat by.
     * @param target The target of the change (who did it).
     */
    public void changeStat(Stat stat, int change, TargetMonster target) {
        if (monsterStatus.hasProtection() && monsterStatus.getProtectionTarget() == ProtectTarget.STATS && target == TargetMonster.TARGET
                && change < 0) {
            Console.writeMessage(Message.MONSTER_IS_PROTECTED_UNAFFECTED, monsterStatus.getMonster().name());
            return;
        }

        statChanges.put(stat, Math.max(-5, Math.min(5, getStat(stat) + change)));

        if (change > 0) {
            Console.writeMessage(Message.MONSTER_STAT_RISES, monsterStatus.getMonster().name(), stat.name());
        } else if (change < 0) {
            Console.writeMessage(Message.MONSTER_STAT_DECREASES, monsterStatus.getMonster().name(), stat.name());
        }
    }

    /**
     * Gets the effective value of a specific stat, taking into account base stats, stat changes, and status conditions.
     *
     * @param stat The stat to retrieve the effective value for.
     * @return The effective value of the stat.
     */
    public double getEffectiveStat(Stat stat) {
        return stat.getBaseStat(monsterStatus.getMonster()) * stat.getStatFactor(getStat(stat))
                * monsterStatus.getStatusConditionManager().getStatusCondition().getMultiplier(stat);
    }

}
