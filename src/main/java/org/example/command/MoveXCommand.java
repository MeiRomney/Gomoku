package org.example.command;

import org.example.model.*;
import org.example.service.BoardService;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Commands for user to move X
 */
public class MoveXCommand extends Command {

    private static final String REGEX = "^MOVE_X\\s+([1-15])\\s+([1-15])$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    private static final Set<GamePhase> ALLOWED = Set.of(GamePhase.PLAYING);

    public MoveXCommand() {
        super("MOVE_X", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input.trim());
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        if(!Boolean.TRUE.equals(state.isHumanPlaysX())) {
            System.out.println("You cannot MOVE_X because you are not playing X");
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
        Position newX = new Position(row, col);

        // Check legality
        List<Move> possible = bs.determineXPossibleMoves();
        boolean ok = possible.stream().anyMatch(mv -> mv.getPosition().equals(newX));
        if(!ok) {
            System.out.println("Error: Move not possible");
            return;
        }

        bs.handleMove(new Move(new XPlayer(bs.getBoard().getXPosition()), newX));
        System.out.println("New X: " + newX.getRow() + " " + newX.getColumn());

        System.out.println("Board after your move: ");
        System.out.println(bs.getBoard());
    }
}
