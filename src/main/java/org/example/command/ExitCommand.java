package org.example.command;

import org.example.model.GamePhase;
import org.example.model.GameState;

import java.util.EnumSet;
import java.util.regex.Matcher;

public class ExitCommand extends Command{
    public ExitCommand() {
        super("Exit", "^EXIT$", EnumSet.allOf(GamePhase.class));
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        state.setExitRequested(true);
        System.out.println("Exiting game...");
    }
}
