package edu.kit.kastel.game.model.status;

import edu.kit.kastel.game.model.StatusCondition;

/**
 * Manages the status condition of a monster, including changes to the condition and its effects. Handles the application, removal, and
 * periodic updates of status conditions.
 *
 * @author uupyx
 */
public class StatusConditionManager {

    private final MonsterStatus monsterStatus;

    private StatusCondition statusCondition;

    /**
     * Constructs a manager for a specific monster.
     *
     * @param monsterStatus The monster status this manager is associated with.
     */
    public StatusConditionManager(MonsterStatus monsterStatus) {
        this.monsterStatus = monsterStatus;

        statusCondition = StatusCondition.OK;

    }

    /**
     * Changes the status condition of the monster, if the current condition allows it.
     *
     * @param statusCondition The new status condition to set.
     */
    public void changeStatusCondition(StatusCondition statusCondition) {
        if (statusCondition != StatusCondition.FAINTED && this.statusCondition != StatusCondition.OK) {
            return;
        }

        this.statusCondition = statusCondition;

        statusCondition.onAdded(monsterStatus);
    }

    /**
     * Attempts to end the current status condition, based on a random decision. If the condition is not ended, it triggers its periodic
     * effect.
     */
    public void tryToEnd() {
        if (statusCondition != StatusCondition.OK && statusCondition != StatusCondition.FAINTED) {
            boolean statusEnds = monsterStatus.getDecisionMaker().decideYesNo(100d / 3d, "status condition end");

            if (statusEnds) {
                statusCondition.onRemoved(monsterStatus);

                statusCondition = StatusCondition.OK;
            } else {
                statusCondition.onTick(monsterStatus);
            }
        }
    }

    /**
     * Gets the current status condition of the monster.
     *
     * @return The current status condition.
     */
    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

}
