package org.example.command;

import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.model.Position;
import org.example.service.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetCommand extends Command {

    private static final String REGEX = "^RESET$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.EDIT
    );

    public ResetCommand() {
        super("RESET", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        // Create empty board
        List<Position> xPositions = new ArrayList<>();
        List<Position> oPositions = new ArrayList<>();
        Board newBoard = new Board(xPositions, oPositions, true);

        // Update state
        state.setBoardService(new BoardService(newBoard));
        state.setHumanPlaysX(true);
        state.setXTurn(true);
        state.setLoadedAGame(false);
        state.setGamePhase(GamePhase.EDIT);

        System.out.println("Board reset to empty board");
        System.out.println(newBoard);
    }
}
