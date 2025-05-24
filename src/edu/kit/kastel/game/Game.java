package edu.kit.kastel.game;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.kit.kastel.config.Config;
import edu.kit.kastel.config.Lexer;
import edu.kit.kastel.config.Parser;
import edu.kit.kastel.config.PositionException;
import edu.kit.kastel.game.console.CommandManager;
import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;
import edu.kit.kastel.game.decision.DebugDecisionMaker;
import edu.kit.kastel.game.decision.DecisionMaker;
import edu.kit.kastel.game.decision.RandomDecisionMaker;
import edu.kit.kastel.game.model.Monster;

/**
 * Main game logic for the monster battle competition. Handles configuration loading, command processing, and game loop execution.
 *
 * @author uupyx
 */
public final class Game {

    private final CommandManager commandManager;
    private final DecisionMaker decisionMaker;

    private Config config;
    private Competition competition;

    private boolean running;

    /**
     * Initializes the game with the given configuration file.
     *
     * @param configPath Path to the configuration file
     * @param seed Optional seed for randomization (null for system time)
     * @param debug If true, enables debug mode with deterministic decision-making
     */
    public Game(String configPath, Long seed, boolean debug) {
        this.commandManager = new CommandManager();
        this.decisionMaker = debug ? new DebugDecisionMaker() : new RandomDecisionMaker(seed != null ? seed : System.currentTimeMillis());
        
        loadConfig(configPath, true); // True, because in the beginning, it will output everything
        
        registerCommands();
    }

    /**
     * Registers all available commands for the game.
     */
    private void registerCommands() {
        commandManager.register(CommandManager.fixed("quit"), args -> {
            stop();
        });

        commandManager.register(CommandManager.fixedCount("load", 1), args -> {
            loadConfig(args[0], true);
        });

        commandManager.register(CommandManager.fixed("show"), args -> {
            if (competition != null) {
                competition.show();
            } else {
                Console.writeMessage(Message.ERROR_NO_COMPETITION);
            }
        });

        commandManager.register(CommandManager.fixed("show", "monsters"), args -> {
            String text = "";
            for (Monster monster : config.monsters().values()) {
                text += monster.toString();
            }
            Console.writeVerbatim(text);
        });

        commandManager.register(CommandManager.fixed("show", "actions"), args -> {
            if (competition != null) {
                competition.showActions();
            } else {
                Console.writeMessage(Message.ERROR_NO_COMPETITION);
            }
        });

        commandManager.register(CommandManager.fixed("show", "stats"), args -> {
            if (competition != null) {
                competition.showStats();
            } else {
                Console.writeMessage(Message.ERROR_NO_COMPETITION);
            }
        });

        commandManager.register(CommandManager.fixed("pass"), args -> {
            if (competition != null) {
                competition.pass();
            } else {
                Console.writeMessage(Message.ERROR_NO_COMPETITION);
            }
        });

        commandManager.register(CommandManager.variableCount("competition", 2), this::competitionCommand);

        commandManager.register(CommandManager.hybrid("action", 1, 1), args -> {
            if (competition == null) {
                Console.writeMessage(Message.ERROR_NO_COMPETITION);
                return; // No competition running
            }
            
            competition.action(args[0], args.length == 1 ? null : args[1]);
        });
    }

    /**
     * Handles the competition command with the given monster names.
     *
     * @param args Names of monsters participating in the competition
     */
    private void competitionCommand(String[] args) {
        Monster[] monsters = new Monster[args.length];
        for (int i = 0; i < args.length; i++) {
            String monsterName = args[i];

            Monster monster = config.monsters().get(monsterName);
            if (monster == null) {
                Console.writeMessage(Message.ERROR_MONSTER_EXIST, monsterName);
                return;
            }

            monsters[i] = monster;
        }

        competition = new Competition(monsters, decisionMaker);
        Console.writeMessage(Message.COMPETITION_START, monsters.length);
    }

    /**
     * Loads a new game configuration from the specified file.
     *
     * @param path Path to the configuration file
     * @param verbatim If true, prints the loaded configuration
     */
    private void loadConfig(String path, boolean verbatim) {
        String content = null;

        try {
            content = Files.readString(Path.of(path));
        } catch (IOException e) {
            Console.writeMessage(Message.ERROR_CONFIG);
            return;
        }

        if (verbatim) {
            Console.writeVerbatim(content); // Verbatim output
            if (content.charAt(content.length() - 1) != '\n') {
                Console.writeLine("");
            }
        }
        
        Config config = null;
        
        Lexer lexer = new Lexer(content);
        Parser parser = null;
        try {         
            parser = new Parser(lexer.tokenize());
            config = parser.parse();
        } catch (PositionException e) {
            Console.writeMessage(Message.ERROR, e.getMessage());
            return;
        }
        
        competition = null;
        this.config = null;
        this.config = config;

        Console.writeMessage(Message.CONFIG_LOADED, config.actions().size(), config.monsters().size());
    }

    /**
     * Starts the game loop, processing commands and handling the competition.
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;

        while (running) {

            if (competition != null && !competition.isFinished()) {
                competition.step();
            }

            commandManager.parse();
        }
    }

    /**
     * Stops the game loop.
     */
    public void stop() {
        if (!running) {
            return;
        }

        running = false;
    }

    /**
     * Main entry point for the game.
     *
     * @param args Command-line arguments: config path, optional seed, or debug mode
     */
    public static void main(String[] args) {
        Console.initialize();

        if (args.length < 1) {
            Console.writeMessage(Message.ERROR_MISSING_CONFIG);
            Console.terminate();
            return;
        } else if (args.length != 2) {
            Console.writeMessage(Message.ERROR_WRONG_ARGS);
            Console.terminate();
            return;
        }

        Long seed = args.length > 1 && !args[1].equals("debug") ? Long.parseLong(args[1]) : null;
        boolean debug = args.length > 1 && args[1].equals("debug");

        Game game = new Game(args[0], seed, debug);
        game.start();

        Console.terminate();
    }

}
