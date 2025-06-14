package edu.kit.kastel.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.kastel.game.model.Action;
import edu.kit.kastel.game.model.Element;
import edu.kit.kastel.game.model.Monster;
import edu.kit.kastel.game.model.ProtectTarget;
import edu.kit.kastel.game.model.Stat;
import edu.kit.kastel.game.model.StatusCondition;
import edu.kit.kastel.game.model.TargetMonster;
import edu.kit.kastel.game.model.count.Count;
import edu.kit.kastel.game.model.count.FixedCount;
import edu.kit.kastel.game.model.count.RandomCount;
import edu.kit.kastel.game.model.effect.ContinueEffect;
import edu.kit.kastel.game.model.effect.DamageEffect;
import edu.kit.kastel.game.model.effect.Effect;
import edu.kit.kastel.game.model.effect.HealEffect;
import edu.kit.kastel.game.model.effect.InflictStatChangeEffect;
import edu.kit.kastel.game.model.effect.InflictStatusConditionEffect;
import edu.kit.kastel.game.model.effect.ProtectStatEffect;
import edu.kit.kastel.game.model.effect.RepeatEffect;
import edu.kit.kastel.game.model.strength.AbsStrength;
import edu.kit.kastel.game.model.strength.BaseStrength;
import edu.kit.kastel.game.model.strength.RelStrength;
import edu.kit.kastel.game.model.strength.Strength;

/**
 * A parser that converts a list of tokens into a structured configuration for the monster battle game. The parser processes tokens
 * generated by the lexer and constructs the game configuration, including actions and monsters. It ensures that all elements are
 * syntactically valid and throws errors when encountering unexpected tokens.
 *
 * @author uupyx
 */
public final class Parser {

    private final List<Token> tokens;
    private int pos = 0;
    private Map<String, Action> actions;
    private Map<String, Monster> monsters;

    /**
     * Constructs a parser with a given list of tokens.
     *
     * @param tokens The list of tokens to be parsed.
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the token list and creates a game configuration.
     *
     * @return A {@link Config} object containing parsed actions and monsters.
     * @throws PositionException If parsing fails due to invalid input.
     */
    public Config parse() throws PositionException {
        actions = new LinkedHashMap<>();
        while (match(TokenType.ACTION)) {
            Action action = parseAction();            
            actions.put(action.name(), action);
        }

        // The LinkedHashMap makes sure, that the insertion order is still there, making it easier to later address monster by their id,
        // instead of their name. Allows to be addressed by name as well as key. Perfect!
        monsters = new LinkedHashMap<>();
        while (match(TokenType.MONSTER)) {
            Monster monster = parseMonster();
            monsters.put(monster.name(), monster);
        }

        return new Config(Collections.unmodifiableMap(actions), Collections.unmodifiableMap(monsters));
    }

    private int parseInteger(String message) throws PositionException {
        Token token = consume(TokenType.INTEGER, message);
        try {
            return Integer.parseInt(token.lexeme());
        } catch (NumberFormatException e) {
            throw error("Invalid integer");
        }
    }

    private Action parseAction() throws PositionException {
        String name = consume(TokenType.IDENTIFIER, "Expected action name").lexeme();

        if (actions.containsKey(name)) {
            throw error("Action '" + name + "' already exists");
        }

        Token elementToken = consumeOneOf("Expected element (WATER, FIRE, EARTH, NORMAL)", TokenType.WATER, TokenType.FIRE, TokenType.EARTH,
                TokenType.NORMAL);
        Element element = Element.valueOf(elementToken.lexeme());

        consumeNewline("Expected newline after action head");

        List<Effect> effects = parseEffects(true, () -> check(TokenType.END));
        if (effects.isEmpty()) {
            throw error("Action with no effects");
        }
        
        consume(TokenType.END, "Expected 'end'");
        consume(TokenType.ACTION, "Expected 'action' after 'end'");
        consumeAtLeastOneNewline("Expected newline after end of action");

        return new Action(name, element, effects);
    }

    private List<Effect> parseEffects(boolean allowRepeat, CheckStopCondition stopCondition) throws PositionException {
        List<Effect> effects = new ArrayList<>();
        while (!stopCondition.check()) {
            while (check(TokenType.NEWLINE)) {
                advance();
            }
            if (stopCondition.check()) {
                break;
            }
            Effect effect = parseEffect();
            if (effect instanceof RepeatEffect && !allowRepeat) {
                throw error("Nested repeat");
            }
            
            effects.add(effect);
            if (!stopCondition.check()) {
                consumeNewline("Expected newline after effect");
            }
        }
        return effects;
    }

    private Effect parseEffect() throws PositionException {
        Token token = peek();
        return switch (token.type()) {
            case DAMAGE -> parseDamageEffect();
            case INFLICT_STATUS_CONDITION -> parseInflictStatusConditionEffect();
            case INFLICT_STAT_CHANGE -> parseInflictStatChangeEffect();
            case PROTECT_STAT -> parseProtectStatEffect();
            case HEAL -> parseHealEffect();
            case REPEAT -> parseRepeatEffect();
            case CONTINUE -> parseContinueEffect();
            default -> throw error("Unknown effect type");
        };
    }

    private DamageEffect parseDamageEffect() throws PositionException {
        advance(); // consume 'damage'
        Token targetToken = consumeOneOf("Expected target (user, target)", TokenType.USER, TokenType.TARGET);
        TargetMonster target = switch (targetToken.type()) {
            case USER -> TargetMonster.USER;
            case TARGET -> TargetMonster.TARGET;
            default -> throw new AssertionError("Unreachable");
        };
        Strength strength = parseStrength();
        int hitRate = parseInteger("Expected hit rate");
        return new DamageEffect(target, strength, hitRate, false);
    }

    private InflictStatusConditionEffect parseInflictStatusConditionEffect() throws PositionException {
        advance(); // consume 'inflictStatusCondition'
        Token targetToken = consumeOneOf("Expected target (user, target)", TokenType.USER, TokenType.TARGET);
        TargetMonster target = switch (targetToken.type()) {
            case USER -> TargetMonster.USER;
            case TARGET -> TargetMonster.TARGET;
            default -> throw new AssertionError("Unreachable");
        };
        Token statusToken = consumeOneOf("Expected status (WET, BURN, QUICKSAND, SLEEP)", TokenType.WET, TokenType.BURN,
                TokenType.QUICKSAND, TokenType.SLEEP);
        StatusCondition condition = StatusCondition.valueOf(statusToken.lexeme());
        int hitRate = parseInteger("Expected hit rate");
        return new InflictStatusConditionEffect(target, condition, hitRate);
    }

    private InflictStatChangeEffect parseInflictStatChangeEffect() throws PositionException {
        advance(); // consume 'inflictStatChange'
        Token targetToken = consumeOneOf("Expected target (user, target)", TokenType.USER, TokenType.TARGET);
        TargetMonster target = switch (targetToken.type()) {
            case USER -> TargetMonster.USER;
            case TARGET -> TargetMonster.TARGET;
            default -> throw new AssertionError("Unreachable");
        };
        Token statToken = consumeOneOf("Expected stat (ATK, DEF, SPD, PRC, AGL)", TokenType.ATK, TokenType.DEF, TokenType.SPD,
                TokenType.PRC, TokenType.AGL);
        Stat stat = Stat.valueOf(statToken.lexeme());
        int change = parseInteger("Expected change");
        int hitRate = parseInteger("Expected hit rate");
        return new InflictStatChangeEffect(target, stat, change, hitRate);
    }

    private ProtectStatEffect parseProtectStatEffect() throws PositionException {
        advance(); // consume 'protectStat'
        Token targetToken = consumeOneOf("Expected health target (health, stats)", TokenType.HEALTH, TokenType.STATS);
        ProtectTarget protectTarget = switch (targetToken.type()) {
            case HEALTH -> ProtectTarget.HEALTH;
            case STATS -> ProtectTarget.STATS;
            default -> throw new AssertionError("Unreachable");
        };
        Count count = parseCount();
        int hitRate = parseInteger("Expected hit rate");
        return new ProtectStatEffect(protectTarget, count, hitRate);
    }

    private HealEffect parseHealEffect() throws PositionException {
        advance(); // consume 'heal'
        Token targetToken = consumeOneOf("Expected target (user, target)", TokenType.USER, TokenType.TARGET);
        TargetMonster target = switch (targetToken.type()) {
            case USER -> TargetMonster.USER;
            case TARGET -> TargetMonster.TARGET;
            default -> throw new AssertionError("Unreachable");
        };
        Strength strength = parseStrength();
        int hitRate = parseInteger("Expected hit rate");

        return new HealEffect(target, strength, hitRate);
    }

    private RepeatEffect parseRepeatEffect() throws PositionException {
        advance(); // consume 'repeat'
        Count count = parseCount();
        consumeNewline("Expected new line after repeat");
        List<Effect> nestedEffects = parseEffects(false, () -> check(TokenType.END));
        
        consume(TokenType.END, "Expected 'end' in repeat block");
        consume(TokenType.REPEAT, "Expected 'repeat' after 'end' in repeat block");
//        consumeAtLeastOneNewline("Expected newline after end of repeat block");
        return new RepeatEffect(count, nestedEffects);
    }

    private ContinueEffect parseContinueEffect() throws PositionException {
        advance(); // consume 'continue'
        int hitRate = parseInteger("Expected hit rate");
        return new ContinueEffect(hitRate);
    }

    private Strength parseStrength() throws PositionException {
        Token token = peek();
        return switch (token.type()) {
            case BASE -> {
                advance();
                int value = parseInteger("Expected base value");
                yield new BaseStrength(value);
            }
            case REL -> {
                advance();
                int percentage = parseInteger("Expected percentage value");
                yield new RelStrength(percentage);
            }
            case ABS -> {
                advance();
                int value = parseInteger("Expected absolute value");
                yield new AbsStrength(value);
            }
            default -> throw error("Expected strength type (base, rel, abs)");
        };
    }

    private Count parseCount() throws PositionException {
        Token token = peek();
        if (token.type() == TokenType.RANDOM) {
            advance(); // consume 'random'
            int min = parseInteger("Expected minimal value for random");
            int max = parseInteger("Expected maximal value for random");
            return new RandomCount(min, max);
        } else {
            int value = parseInteger("Expected count value");
            return new FixedCount(value);
        }
    }

    private Monster parseMonster() throws PositionException {
        String name = consume(TokenType.IDENTIFIER, "Expected monster name").lexeme();
        if (monsters.containsKey(name)) {
            throw error("Monster '" + name + "' already exists");
        }

        Token elementToken = consumeOneOf("Expected element (WATER, FIRE, EARTH, NORMAL)", TokenType.WATER, TokenType.FIRE, TokenType.EARTH,
                TokenType.NORMAL);
        Element element = Element.valueOf(elementToken.lexeme());
        int maxHealth = parseInteger("Expected max_health");
        int baseAttack = parseInteger("Expected base_attack");
        int baseDefense = parseInteger("Expected base_defense");
        int baseSpeed = parseInteger("Expected base_speed");

        List<Action> actions = new ArrayList<>();
        do {
            if (actions.size() == 4) {
                throw error("Too many attacks");
            }
            
            String actionName = consume(TokenType.IDENTIFIER, "Expected action name for monster").lexeme();
            if (!this.actions.containsKey(actionName)) {
                throw error("Monster '" + name + "' references unknown action '" + actionName + "'");
            }
            actions.add(this.actions.get(actionName));
        } while (!check(TokenType.NEWLINE) && !isAtEnd());

        if (!isAtEnd()) {
            consumeAtLeastOneNewline("Expected new line after monster decleration");
        }

        return new Monster(monsters.size() + 1, name, element, maxHealth, baseAttack, baseDefense, baseSpeed,
                Collections.unmodifiableList(actions));
    }

    private Token consumeOneOf(String message, TokenType... types) throws PositionException {
        for (TokenType type : types) {
            if (check(type)) {
                return advance();
            }
        }
        throw error(message);
    }

    private Token consume(TokenType type, String message) throws PositionException {
        if (check(type)) {
            return advance();
        }
        throw error(message);
    }

    private void consumeNewline(String message) throws PositionException {
        if (!check(TokenType.NEWLINE)) {
            throw error(message);
        }
        advance();
    }

    private void consumeAtLeastOneNewline(String message) throws PositionException {
        if (!check(TokenType.NEWLINE)) {
            throw error(message);
        }
        while (check(TokenType.NEWLINE)) {
            advance();
        }
    }

    private boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }

    private PositionException error(String message) {
        Token token = peek();
        return new PositionException(message, token.line(), token.column());
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            pos++;
        }

        return tokens.get(pos - 1);
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    /**
     * Functional interface for defining a stop condition when parsing effects
     */
    private interface CheckStopCondition {
        boolean check();
    }
}
