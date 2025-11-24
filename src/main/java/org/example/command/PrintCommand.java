package org.example.command;

import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.service.BoardService;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Print data
 */
public class PrintCommand extends Command {

    private static final String REGEX = "^PRINT$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final EnumSet<GamePhase> ALLOWED = EnumSet.allOf(GamePhase.class);

    public PrintCommand() {
        super("PRINT", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        System.out.println("=== GAME STATE ===");
        System.out.println("Phase: " + state.getGamePhase());
        System.out.println();

        // Print side selection
        Boolean humanPlaysX = state.isHumanPlaysX();
        if(humanPlaysX) {
            System.out.println("Player side: X");
        } else {
            System.out.println("Player side: O");
        }
        System.out.println();

        // Print whose turn
        Boolean isXTurn = state.isXTurn();
        if(isXTurn) {
            System.out.println("Player turn: X");
        } else {
            System.out.println("Player turn: O");
        }
        System.out.println();

        // Print board
        BoardService bs = state.getBoardService();
        if(bs == null) {
            System.out.println("Board: (No board defined)");
        } else {
            Board board = bs.getBoard();
            if(board == null) {
                System.out.println("Board: (Board service exists but no board defined)");
            } else {
                System.out.println("Board: ");
                System.out.println(board);
            }
        }
        System.out.println();
        System.out.println("========================");
    }
}
