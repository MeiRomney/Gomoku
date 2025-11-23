package org.example;

import org.example.database.DbInit;
import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.model.Position;
import org.example.service.GameService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DbInit.initDb();
        // Create initial Game state
        GameState state = new GameState();
        state.setGamePhase(GamePhase.EDIT);

        // Create and run the engine
        GameService gameService = new GameService(state);

        // Start the game loop (based on commands and phases)
        gameService.run();
        DbInit.stopWebServer();

        // When run() returns, the game is fully finished
        System.out.println("Game terminated.");
    }
}