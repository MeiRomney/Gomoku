package org.example.command;

import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.service.BoardService;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewStartBoardCommand extends Command {

    private static final String REGEX = "^NEW_START_BOARD$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.EDIT
    );

    public NewStartBoardCommand() {
        super("NEW_START_BOARD", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        Board starter = BoardService.getEmptyBoard();
        state.setBoardService(new BoardService(starter));
        state.setGamePhase(GamePhase.HAVE_BOARD);

        System.out.println("Created usual starter board");
        System.out.println(starter);
    }
}
