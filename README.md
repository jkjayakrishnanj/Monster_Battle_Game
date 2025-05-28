# Monster_Battle_Game
This project is a fully-featured, terminal-based, turn-based monster battle simulator. Monsters with elemental types and custom actions face off in strategic battles with stat manipulation, damage calculation, and status effects. The game loads configuration from a custom-designed DSL parsed by a lexer and parser built from scratch.
## Architecture & Implementation
Modular Design: Clear separation between configuration parsing, gameplay, combat logic, and user interaction.

OOP Principles: Extensive use of record, enum, and interfaces to represent game elements like Action, Stat, Effect, etc.

State Management: Each monster's state is encapsulated in MonsterStatus, which manages health, stat boosts/nerfs, status conditions, and protection.

Turn Phases: Game loop is divided into selection and execution phases handled via Competition.step().
