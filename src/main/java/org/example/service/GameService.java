package org.example.service;

import org.example.command.Command;
import org.example.command.CommandRegistry;
import org.example.model.GamePhase;
import org.example.model.GameState;

import java.util.List;
import java.util.Scanner;

public class GameService implements Runnable {
    private final Scanner scanner = new Scanner(System.in);
    private final GameState state;
    private final List<Command> commands;

    public GameService(GameState state) {
        this.state = state;
        this.commands = new CommandRegistry().getAllCommands();
    }

    @Override
    public void run() {
        System.out.println("=== Gomoku Game Service ===");
        System.out.println("Current phase: " + state.getGamePhase());

        while(!state.isExitRequested()) {
            showPossibleCommands();

            System.out.print("> ");
            String input = scanner.nextLine().trim();

            Command matching = findMatchingCommand(input);

            if(matching == null) {
                System.out.println("Invalid command or not allowed in this phase. Try again.");
                continue;
            }

            matching.apply(state, matching.match(input));

            autoDetectEndOfGame();
        }
        System.out.println("=== Game terminated ===");
    }

    private void showPossibleCommands() {
        System.out.println();
        System.out.println("Phase: " + state.getGamePhase());
        System.out.println("Available commands: ");

        commands.stream()
                .filter(cmd -> cmd.isApplicableInPhase(state.getGamePhase()))
                .forEach(cmd -> System.out.println(" - " + cmd.getName()));
    }

    private Command findMatchingCommand(String input) {
        for(Command cmd : commands) {
            if(cmd.isApplicableInPhase(state.getGamePhase()) && cmd.match(input) != null) {
                return cmd;
            }
        }
        return null;
    }

    private void autoDetectEndOfGame() {
        if(state.getGamePhase() != GamePhase.PLAYING) return;
        if(state.getBoardService() == null) return;

        var bs = state.getBoardService();

        if(bs.xWins()) {
            System.out.println("X wins! Game Over.");
            state.setGamePhase(GamePhase.GAME_ENDED);
        } else if(bs.oWins()) {
            System.out.println("O wins! Game Over.");
            state.setGamePhase(GamePhase.GAME_ENDED);
        }
    }
}
