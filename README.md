# Gomoku Game

A console-based **Gomoku** (Five in a Row) game implemented in Java, featuring a modular command system, in-memory database support, and automated game state management.


## Table of Contents

* [Overview]
* [Features]
* [Project Structure]
* [Getting Started]
* [Commands]
* [Database]
* [License]


## Overview

This project is a fully interactive **Gomoku** game that allows players to play against each other or against the machine. The game supports phase-based commands, automated win detection, and an in-memory H2 database to save and load game states.


## Features

* **Phase-based gameplay**: Commands are only available in specific game phases.
* **Command system**: Easily extendable commands architecture.
* **Automatic end-of-game detection** for both players.
* **In-memory database** using H2 for saving and loading game states.
* **Machine move support** for AI gameplay.
* **Restart and reset functionality** for convenience.


## Project Structure

src/main/java/org/example/
│
├── command/          # All command classes (Move, Exit, Save, etc.)
├── database/         # Database initialization and management
├── model/            # Game state and enums (GamePhase, GameState)
└── service/          # GameService: main game loop and logic

**Key Classes:**

* `GameService` – Main game loop, reads commands, and updates the game state.
* `Command` – Abstract class representing a command in the game.
* `CommandRegistry` – Registers all commands and finds matching commands for input.
* `DbInit` – Initializes H2 in-memory database and provides JDBC URL.


## Getting Started

### Prerequisites

* Java 17+
* Maven (or any build tool that supports Java)

### Running the Game

1. Clone the repository:

git clone https://github.com/yourusername/gomoku-game.git
cd gomoku-game

2. Build the project:

mvn clean install

3. Run the game:

java -cp target/gomoku-game-1.0-SNAPSHOT.jar org.example.service.GameService


## Commands

The game uses a **phase-based command system**. Available commands depend on the current game phase. Some examples:

* **Global Commands**

  * `exit` – Terminate the game
  * `restart` – Restart the current game
  * `print` – Print the current board

* **Database Commands**

  * `save <filename>` – Save the current game state
  * `load <filename>` – Load a previously saved game
  * `list` – List all saved games

* **Game Commands**

  * `move x <row> <col>` – Make a move as player X
  * `move o <row> <col>` – Make a move as player O
  * `machine move` – Let AI make a move

*Commands only available in their allowed game phase.*


## Database

The project uses an **H2 in-memory database**:

* Automatically initialized on game start.
* Accessible via [H2 console](http://localhost:8082) on port 8082.
* Schema created from `/resources/createDbScript.txt`.

**Database URL:**

jdbc:h2:mem:gomoku;DB_CLOSE_DELAY=-1


## Author

Mei Romney


<img width="1918" height="1015" alt="image" src="https://github.com/user-attachments/assets/33c2ebc5-d9b3-4e3b-8c85-3d06d2b1977b" />

