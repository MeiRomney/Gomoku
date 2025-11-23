package org.example.command;

import org.example.model.GamePhase;
import org.example.model.GameState;

import java.util.EnumSet;
import java.util.regex.Matcher;

public class RestartCommand extends Command {

    public RestartCommand() {
        super("RESTART", "^RESTART$", EnumSet.of(GamePhase.SAVED_GAME, GamePhase.GAME_ENDED));
    }

    @Override
    public void apply(GameState state, Matcher matcher) {

        // Reset game state but keeps DB running
        state.setBoardService(null);
        state.setHumanPlaysX(true);
        state.setXTurn(true);
        state.setLoadedAGame(false);

        // Move the flow back to DB_INITED
        state.setGamePhase(GamePhase.EDIT);

        System.out.println("Game restarted. Returning to main menu (Edit Phase).");
    }
}