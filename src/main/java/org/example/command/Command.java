package org.example.command;

import org.example.model.GamePhase;
import org.example.model.GameState;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create abstract class for commands
 */
public abstract class Command {
    private final Pattern pattern;
    private final String name;

    protected Set<GamePhase> allowedPhases;

    protected Command(String name, String regex, Set<GamePhase> allowedPhases) {
        this.name = name;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.allowedPhases = EnumSet.copyOf(allowedPhases);
    }

    public Matcher match(String input) {
        Matcher m = pattern.matcher(input.trim());
        return m.matches() ? m : null;
    }

    public boolean isApplicableInPhase(GamePhase phase) {
        return allowedPhases.contains(phase);
    }

    public abstract void apply(GameState state, Matcher matcher);

    public String getSyntax() {
        return pattern.pattern();
    }

    public String getName() {
        return name;
    }
}
