package edu.kit.kastel.game.model;

import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Represents the two possible target monsters in the game: the user and the target.
 *
 * @author uupyx
 */
public enum TargetMonster {
    /**
     * The user.
     */
    USER,
    /**
     * The target.
     */
    TARGET;

    /**
     * Determines the target of the effect.
     *
     * @param user The monster performing the action.
     * @param target The opponent monster.
     * @return The monster status that is the target of the effect (user or target).
     */
    public MonsterStatus get(MonsterStatus user, MonsterStatus target) {
        return (this == USER) ? user : target;
    }

}
