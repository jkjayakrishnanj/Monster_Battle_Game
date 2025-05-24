package edu.kit.kastel.game.model;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Represents possible status conditions a monster can have.
 * <p>
 * A monster can have only one status condition at a time. Status conditions influence the monster's effectiveness in battle:
 * <ul>
 * <li>WET - Reduces DEF by 25%</li>
 * <li>BURN - Reduces ATK by 25% and deals 10% max HP damage at the end of each turn</li>
 * <li>QUICKSAND - Reduces SPD by 25%</li>
 * <li>SLEEP - Prevents the monster from acting</li>
 * </ul>
 * (See Spec A.2.5 for details.)
 *
 * @author uupyx
 */
public enum StatusCondition {

    /**
     * Monster is okay.
     */
    OK,

    /**
     * Monster is fainted.
     */
    FAINTED {
        @Override
        public boolean preventsAction() {
            return true;
        }
    },

    /**
     * Monster is wet.
     */
    WET {
        @Override
        public double getMultiplier(Stat stat) {
            return (stat == Stat.DEF) ? 0.75 : super.getMultiplier(stat);
        }

        @Override
        public void onRemoved(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_DRIED, status.getMonster().name());
        }

        @Override
        public void onAdded(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_BECOMES_WET, status.getMonster().name());
        }

        @Override
        public void onTick(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_IS_WET, status.getMonster().name());
        }
    },

    /**
     * Monster is burning.
     */
    BURN {
        @Override
        public double getMultiplier(Stat stat) {
            return (stat == Stat.ATK) ? 0.75 : super.getMultiplier(stat);
        }

        @Override
        public void applyEndOfTurnEffect(MonsterStatus status) {
            status.damage(TargetMonster.TARGET, status.getDamageCalculator().calculateBurnDamage(), true); // Burn
        }

        @Override
        public void onRemoved(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_BURNING_FADED, status.getMonster().name());
        }

        @Override
        public void onAdded(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_CAUGHT_FIRE, status.getMonster().name());
        }

        @Override
        public void onTick(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_BURNING, status.getMonster().name());
        }
    },

    /**
     * Monster is in quicksand.
     */
    QUICKSAND {
        @Override
        public double getMultiplier(Stat stat) {
            return (stat == Stat.SPD) ? 0.75 : super.getMultiplier(stat);
        }

        @Override
        public void applyEndOfTurnEffect(MonsterStatus status) {

        }

        @Override
        public void onRemoved(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_ESCAPED_QUICKSAND, status.getMonster().name());
        }

        @Override
        public void onAdded(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_CAUGHT_QUICKSAND, status.getMonster().name());
        }

        @Override
        public void onTick(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_IS_QUICKSAND, status.getMonster().name());
        }
    },

    /**
     * Monster is sleeping.
     */
    SLEEP {
        @Override
        public boolean preventsAction() {
            return true;
        }

        @Override
        public void applyEndOfTurnEffect(MonsterStatus status) {

        }

        @Override
        public void onRemoved(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_WOKE_UP, status.getMonster().name());
        }

        @Override
        public void onAdded(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_FALLS_ASLEEP, status.getMonster().name());
        }

        @Override
        public void onTick(MonsterStatus status) {
            Console.writeMessage(Message.MONSTER_IS_ASLEEP, status.getMonster().name());
        }
    };

    /**
     * Gives back the multiplier, based on the stat.
     *
     * @param stat The stat
     * @return The multiplier (1.0) if no status condition
     */
    public double getMultiplier(Stat stat) {
        return 1.0;
    }

    /**
     * Determines if this status condition prevents the monster from acting.
     *
     * @return true, if the monster cannot act, false otherwise
     */
    public boolean preventsAction() {
        return false;
    }

    /**
     * Applies end-of-turn-effects, such as burn damage.
     *
     * @param status The monsters status to apply effects to.
     */
    public void applyEndOfTurnEffect(MonsterStatus status) {

    }

    /**
     * Actions performed when the status is added.
     *
     * @param status The monster's status being added.
     */
    public void onAdded(MonsterStatus status) {

    }

    /**
     * Actions performed when the status is removed.
     *
     * @param status The monster's status being removed.
     */
    public void onRemoved(MonsterStatus status) {

    }

    /**
     * Actions performed each tick of the status.
     *
     * @param status The monster's status each tick.
     */
    public void onTick(MonsterStatus status) {

    }

}
