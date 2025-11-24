package org.example.command;

import org.example.model.GamePhase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

/**
 * Class for registering commands
 */
public class CommandRegistry {

    private final List<Command> commands = new ArrayList<>();

    /**
     * Constructors
     */
    public CommandRegistry() {
        // Global commands
        commands.add(new ExitCommand());
        commands.add(new RestartCommand());
        commands.add(new PrintCommand());

        // Database / Load commands
        commands.add(new ListCommand());
        commands.add(new LoadCommand());

        // Editing commands
        commands.add(new ResetCommand());
        commands.add(new NewStartBoardCommand());

        // Side selection commands
        commands.add(new PlayXCommand());
        commands.add(new PlayOCommand());

        // Playing commands
        commands.add(new MoveXCommand());
        commands.add(new MoveOCommand());
        commands.add(new MachineMoveCommand());

        // Save command
        commands.add(new SaveCommand());
    }

    public List<Command> getAllCommands() {
        return commands;
    }

    /**
     * Get allowed commands for any specific phase
     * @param phase
     * @return commands
     */
    public List<Command> getCommandsForPhase(GamePhase phase) {
        List<Command> result = new ArrayList<>();
        for(Command command : commands) {
            if(command.isApplicableInPhase(phase)) {
                result.add(command);
            }
        }
        return result;
    }

    /**
     * Finding commands that match input of the user
     * @param input
     * @param phase
     * @return commands
     */
    public MatchResult findMatchingCommand(String input, GamePhase phase) {
        for(Command command : commands) {
            if(!command.isApplicableInPhase(phase)) continue;

            Matcher m = command.match(input);
            if(m != null) {
                return new MatchResult(command, m);
            }
        }
        return null;
    }

    public static class MatchResult {
        public final Command command;
        public final Matcher matcher;

        public MatchResult(Command command, Matcher matcher) {
            this.command = command;
            this.matcher = matcher;
        }
    }
}
