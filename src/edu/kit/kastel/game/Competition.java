package edu.kit.kastel.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.decision.DecisionMaker;
import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Monster;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.effect.DamageEffect;
import edu.kit.kastel.game.model.effect.Effect;
import edu.kit.kastel.game.model.effect.HittableEffect;
import edu.kit.kastel.game.model.effect.RepeatEffect;
import edu.kit.kastel.game.model.status.MonsterStatus;

/**
 * Manages the competition between monsters in the battle.
 *
 * @author uupyx
 */
public final class Competition {

    private final List<MonsterStatus> monsters;
    private boolean finished;
    private int currentSelectingMonsterIndex;
    private boolean phaseIComplete;
    private Map<MonsterStatus, Action> selectedActions;
    private Map<MonsterStatus, MonsterStatus> selectedTargets;

    /**
     * Constructs a new competition with the provided monsters and decision maker.
     *
     * @param monsters The list of monsters participating in the competition.
     * @param decisionMaker The decision maker used for selecting actions.
     */
    public Competition(Monster[] monsters, DecisionMaker decisionMaker) {
        this.monsters = new ArrayList<>();
        this.finished = false;
        this.currentSelectingMonsterIndex = 0;
        this.phaseIComplete = false;
        this.selectedActions = new HashMap<>();
        this.selectedTargets = new HashMap<>();
        
        Map<String, Integer> nameOccurrences = countNameOccurrences(monsters);
        Map<String, Integer> nameCounters = new HashMap<>();
        for (Monster monster : monsters) {
            String baseName = monster.name();
            String displayName;
            if (nameOccurrences.get(baseName) > 1) {
                int counter = nameCounters.getOrDefault(baseName, 0) + 1;
                nameCounters.put(baseName, counter);
                displayName = baseName + "#" + counter;
            } else {
                displayName = baseName;
            }
            Monster adjustedMonster = new Monster(monster.id(), displayName, monster.element(), monster.maxHealth(), monster.baseAttack(),
                    monster.baseDefense(), monster.baseSpeed(), monster.actions());
            this.monsters.add(new MonsterStatus(adjustedMonster, decisionMaker));
        }
    }

    private Map<String, Integer> countNameOccurrences(Monster[] monsters) {
        Map<String, Integer> nameOccurrences = new HashMap<>();
        for (Monster monster : monsters) {
            String baseName = monster.name();
            nameOccurrences.put(baseName, nameOccurrences.getOrDefault(baseName, 0) + 1);
        }

        return nameOccurrences;
    }

    /**
     * Performs one step in the competition.
     */
    public void step() {
        // Phase 0
        if (checkIfCompetitionEnded()) {
            return;
        }

        if (phaseIComplete) {
            executeActions(); // Phase II
        } else {
            selectActions(); // Phase I
        }
    }

    /**
     * Checks if the competition has ended and determines the winner if applicable.
     *
     * @return true if the competition has ended, otherwise false.
     */
    private boolean checkIfCompetitionEnded() {
        int activeMonstersCount = 0;
        MonsterStatus winner = null;

        // Count active monsters and store the last non-fainted monster
        for (MonsterStatus m : monsters) {
            if (!m.isFainted()) {
                activeMonstersCount++;
                winner = m; // Keep track of the last active monster
            }
        }

        if (activeMonstersCount < 2) {
            finished = true;
            if (activeMonstersCount == 1) {
                Console.writeMessage(Message.MONSTER_WIN, winner.getMonster().name());
            } else {
                Console.writeMessage(Message.MONSTER_DRAW);
            }
            return true;
        }

        return false;
    }

    /**
     * Executes the selected actions in order of speed.
     */
    private void executeActions() {
        List<MonsterStatus> sortedMonsters = getSortedMonstersBySpeed();

        for (MonsterStatus monster : sortedMonsters) {
            if (monster.isFainted()) {
                continue;
            }

            Console.writeMessage(Message.MONSTERS_TURN, monster.getMonster().name());
            executeActionForMonster(monster);
        }
        
        for (MonsterStatus monster : sortedMonsters) {
            monster.decrementProtectionRounds();
        }
        
        resetForNextRound();
    }

    /**
     * Gets the list of active monsters sorted by speed.
     *
     * @return Sorted list of monsters.
     */
    private List<MonsterStatus> getSortedMonstersBySpeed() {
        List<MonsterStatus> filteredMonsters = new ArrayList<>();

        // Filter out fainted monsters
        for (MonsterStatus m : monsters) {
            if (!m.isFainted()) {
                filteredMonsters.add(m);
            }
        }

        // Sort in descending order of speed
        filteredMonsters.sort(
                (m1, m2) -> Double.compare(m2.getStatManager().getEffectiveStat(Stat.SPD), m1.getStatManager().getEffectiveStat(Stat.SPD)));

        return filteredMonsters;
    }

    /**
     * Executes an action for a given monster.
     *
     * @param monster The monster whose action will be executed.
     */
    private void executeActionForMonster(MonsterStatus monster) {
        Action action = selectedActions.get(monster);
        MonsterStatus target = selectedTargets.get(monster);
        
        monster.performAction(action, target);
    }

    /**
     * Resets the state for the next round.
     */
    private void resetForNextRound() {
        selectedActions.clear();
        selectedTargets.clear();
        currentSelectingMonsterIndex = 0;
        phaseIComplete = false;
        if (!finished) {
            step();
        }
    }

    /**
     * Handles the action selection phase.
     */
    private void selectActions() {
        if (currentSelectingMonsterIndex < monsters.size()) {
            MonsterStatus monster = monsters.get(currentSelectingMonsterIndex);
            if (!monster.isFainted()) {
                Console.writeMessage(Message.WHAT_ACTION, monster.getMonster().name());
                return;
            }
            currentSelectingMonsterIndex++;
            step();
        } else {
            phaseIComplete = true;
            step();
        }
    }

    /**
     * Checks if the competition has finished.
     *
     * @return True if the competition has finished, false otherwise.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Displays the current status of all monsters.
     */
    public void show() {
        for (int i = 0; i < monsters.size(); i++) {
            MonsterStatus monster = monsters.get(i);
            double maxHealth = monster.getMonster().maxHealth();
            double healthRatio = monster.getHealth() / maxHealth;

            double scaled = healthRatio * 20;
            int amount = (int) Math.ceil(scaled);

            int empty = 20 - amount;

            String bar = "[" + "X".repeat(amount) + "_".repeat(empty) + "]";

            String name = (i == currentSelectingMonsterIndex && !phaseIComplete) ? "*" + monster.getMonster().name()
                    : monster.getMonster().name();
            String status = monster.getStatusConditionManager().getStatusCondition().name();

            Console.writeLine(bar + " " + (i + 1) + " " + name + " (" + status + ")");
        }
    }

    /**
     * Displays the available actions of the currently selecting monster.
     */
    public void showActions() {
        if (currentSelectingMonsterIndex >= monsters.size()) {
            return;
        }
        MonsterStatus currentMonster = monsters.get(currentSelectingMonsterIndex);
        Console.writeMessage(Message.ACTIONS_OF_MONSTER, currentMonster.getMonster().name());

        for (Action action : currentMonster.getMonster().actions()) {
            String strengthString = "--";
            int hitRate = -1;

            for (Effect effect : action.effects()) {
                if (hitRate == -1) {
                    if (effect instanceof HittableEffect hittable) {
                        hitRate = hittable.getHitRate();
                    } else if (effect instanceof DamageEffect damageEffect) {
                        hitRate = damageEffect.getHitRate();
                    }
                }
                if (strengthString.equals("--") && effect instanceof DamageEffect damageEffect) {
                    strengthString = damageEffect.strength().toString();
                }
                if (effect instanceof RepeatEffect repeatEffect) {
                    for (Effect nextEffect : repeatEffect.effects()) {
                        if (hitRate == -1) {
                            if (nextEffect instanceof HittableEffect hittable) {
                                hitRate = hittable.getHitRate();
                            } else if (nextEffect instanceof DamageEffect damageEffect) {
                                hitRate = damageEffect.getHitRate();
                            }
                        }
                        if (strengthString.equals("--") && nextEffect instanceof DamageEffect damageEffect) {
                            strengthString = damageEffect.strength().toString();
                        }
                        if (hitRate != -1 && !strengthString.equals("--")) {
                            break;
                        }
                    }
                }
                if (hitRate != -1 && !strengthString.equals("--")) {
                    break;
                }
            }

            Console.writeMessage(Message.ACTION_DETAIL, action.name(), action.element().name(), strengthString, hitRate);
        }
    }

    /**
     * Displays the stats of the currently selecting monster.
     */
    public void showStats() {
        MonsterStatus monster = monsters.get(currentSelectingMonsterIndex);

        String atk = Stat.ATK.formatStat(monster);
        String def = Stat.DEF.formatStat(monster);
        String spd = Stat.SPD.formatStat(monster);
        String prc = Stat.PRC.formatStat(monster);
        String agl = Stat.AGL.formatStat(monster);

        Console.writeMessage(Message.SHOW_STATS, monster.getMonster().name(), monster.getHealth(), monster.getMonster().maxHealth(), atk,
                def, spd, prc, agl);
    }

    /**
     * Passes the turn for the current monster and moves to the next one.
     */
    public void pass() {
        if (currentSelectingMonsterIndex >= monsters.size()) {
            return;
        }
        MonsterStatus currentMonster = monsters.get(currentSelectingMonsterIndex);
        selectedActions.put(currentMonster, null); // No action selected
        
        currentSelectingMonsterIndex++;
        if (currentSelectingMonsterIndex >= monsters.size()) {
            phaseIComplete = true;
        }
    }

    /**
     * Selects an action and target for the current monster.
     *
     * @param actionName The name of the action to be performed.
     * @param targetName The name of the target for the action.
     */
    public void action(String actionName, String targetName) {
        if (currentSelectingMonsterIndex >= monsters.size()) {
            return;
        }
        MonsterStatus currentMonster = monsters.get(currentSelectingMonsterIndex);
        Action action = null;
        for (Action a : currentMonster.getMonster().actions()) {
            if (a.name().equals(actionName)) {
                action = a;
                break; // Stop searching once we find the first match
            }
        }
        if (action == null) {
            Console.writeMessage(Message.ERROR_DOES_NOT_KNOW_ACTION, currentMonster.getMonster().name(), actionName);
            return;
        }

        selectedActions.put(currentMonster, action);
        // Determine the target for the action
        List<MonsterStatus> opponents = new ArrayList<>();
        for (MonsterStatus m : monsters) {
            if (m != currentMonster && !m.isFainted()) {
                opponents.add(m);
            }
        }
        if (opponents.isEmpty()) {
            Console.writeMessage(Message.ERROR_NOT_VALID_OPPONENT, currentMonster.getMonster().name());

            selectedActions.put(currentMonster, null); // Invalid action
            currentSelectingMonsterIndex++;
            return;
        }
        if (opponents.size() == 1) {
            selectedTargets.put(currentMonster, opponents.get(0));
        } else if (targetName != null) {
            MonsterStatus target = null;

            // Manually search for the first matching opponent
            for (MonsterStatus m : opponents) {
                if (m.getMonster().name().equals(targetName)) {
                    target = m;
                    break; // Stop searching once found
                }
            }
            if (target == null) {
                Console.writeMessage(Message.ERROR_NOT_VALID_OPPONENT, targetName);
                return;
            }

            selectedTargets.put(currentMonster, target);
        } else {
            Console.writeMessage(Message.ERROR_SPECIFY_TARGET);
            return;
        }

        currentSelectingMonsterIndex++;
        if (currentSelectingMonsterIndex >= monsters.size()) {
            phaseIComplete = true;
        }
    }
}