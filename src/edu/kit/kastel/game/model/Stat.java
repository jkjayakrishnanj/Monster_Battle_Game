package edu.kit.kastel.game.model;

import edu.kit.kastel.game.model.status.MonsterStatus;
import edu.kit.kastel.game.model.status.StatManager;

/**
 * Represents the different stats a monster can have.
 * <p>
 * Stats influence various aspects of battle performance:
 * <ul>
 * <li>ATK - Affects damage dealt</li>
 * <li>DEF - Affects damage taken</li>
 * <li>SPD - Determines turn order</li>
 * <li>PRC - Affects accuracy of attacks</li>
 * <li>AGL - Affects evasion against enemy attacks</li>
 * </ul>
 * These values can be modified during battle (see Spec A.2.3).
 *
 * @author uupyx
 */
public enum Stat {
    /**
     * ATK.
     */
    ATK,
    /**
     * DEF.
     */
    DEF,
    /**
     * SPD.
     */
    SPD,
    /**
     * PRC.
     */
    PRC,
    /**
     * AGL.
     */
    AGL;

    /**
     * Gets the base stat value for a given monster.
     *
     * @param monster The monster whose stat value is to be retrieved.
     * @return The base value of the stat for the monster.
     */
    public int getBaseStat(Monster monster) {
        return switch (this) {
            case ATK -> monster.baseAttack();
            case DEF -> monster.baseDefense();
            case SPD -> monster.baseSpeed();
            default -> 1; // PRC and AGL are 1 by default
        };
    }

    /**
     * Calculates the stat factor based on the change in the stat value.
     *
     * @param n The change in stat value.
     * @return The modified stat factor.
     */
    public double getStatFactor(int n) {
        int baseFactor = switch (this) {
            case ATK, DEF, SPD -> 2;
            case PRC, AGL -> 3;
        };

        if (n > 0) {
            return (double) (baseFactor + n) / baseFactor;
        } else if (n < 0) {
            return (double) baseFactor / (baseFactor - n);
        } else {
            return 1;
        }
    }

    /**
     * Formats the stat value for the given monster.
     *
     * @param monster The monster.
     * @return A formatted string.
     */
    public String formatStat(MonsterStatus monster) {
        StatManager statManager = monster.getStatManager();
        int statValue = statManager.getStat(this);
        int baseValue = getBaseStat(monster.getMonster());

        return baseValue + (statValue == 0 ? "" : "(" + (statValue > 0 ? "+" : "") + statValue + ")");
    }
}
