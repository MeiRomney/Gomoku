package org.example.command;

import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.service.BoardService;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayXCommand extends Command {

    private static final String REGEX = "^PLAY_X$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.HAVE_BOARD
    );

    public PlayXCommand() {
        super("PLAY_X", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches()? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        BoardService service = state.getBoardService();
        if(service == null) {
            System.out.println("Error: there is no board loaded or created");
            return;
        }

        // Set Human side
        state.setHumanPlaysX(true);
        // X starts
        state.setXTurn(true);
        state.setGamePhase(GamePhase.PLAYING);

        System.out.println("You will play X.");
        System.out.println("Game is now in PLAYING Phase.");
        System.out.println("Current board:\n" + service.getBoard());
    }
}
