package org.example.command;

import org.example.model.*;
import org.example.service.BoardService;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveOCommand extends Command {
    private static final String REGEX = "^MOVE_O\\s+([1-15])\\s+([1-15])$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final Set<GamePhase> ALLOWED = Set.of(GamePhase.PLAYING);

    public MoveOCommand() {
        super("MOVE_O", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input.trim());
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        if(Boolean.TRUE.equals(state.isHumanPlaysX())) {
            System.out.println("You cannot MOVE_O because you are not playing O");
            return;
        }

        BoardService bs = state.getBoardService();
        if(bs == null || bs.getBoard() == null) {
            System.out.println("Error: Board is not initialized");
            return;
        }

        // Read coordinates (1-15 from user -> 0-14 internally)
        int row = Integer.parseInt(matcher.group(1)) - 1;
        int col = Integer.parseInt(matcher.group(2)) - 1;
        Position newO = new Position(row, col);

        // Check legality
        List<Move> possible = bs.determineOPossibleMoves();
        boolean ok = possible.stream().anyMatch(mv -> mv.getPosition().equals(newO));
        if(!ok) {
            System.out.println("Error: Move not possible");
            return;
        }

        bs.handleMove(new Move(new OPlayer(bs.getBoard().getOPosition()), newO));
        System.out.println("New O: " + newO.getRow() + " " + newO.getColumn());

        System.out.println("Board after your move: ");
        System.out.println(bs.getBoard());
    }
}
