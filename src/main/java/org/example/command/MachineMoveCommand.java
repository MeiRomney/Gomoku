package org.example.command;

import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.model.Move;
import org.example.service.BoardService;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineMoveCommand extends Command {

    private static final String REGEX = "^MACHINE$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
      GamePhase.PLAYING
    );

    public MachineMoveCommand() {
        super("MACHINE", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        if(state.getBoardService() == null) {
            System.out.println("Error: No board is available");
            return;
        }

        BoardService bs = state.getBoardService();
        boolean machinePlaysX = !state.isHumanPlaysX();
        System.out.println(machinePlaysX ? "Machine plays X" : "Machine plays O");
        Move chosenMove = null;

        if(machinePlaysX) {
            List<Move> xMoves = bs.determineXPossibleMoves();
            if(xMoves.isEmpty()) {
                System.out.println("Machine (X) has no legal move.");
                return;
            }
            chosenMove = xMoves.get(new java.util.Random().nextInt(xMoves.size()));
        } else {
            List<Move> oMoves = bs.determineOPossibleMoves();
            if(oMoves.isEmpty()) {
                System.out.println("Machine (O) has no legal move.");
                return;
            }
            chosenMove = oMoves.get(new java.util.Random().nextInt(oMoves.size()));
        }

        System.out.printf("Machine moves (%s): %d, %d", machinePlaysX ? "X" : "O",
                chosenMove.getPosition().getRow(), chosenMove.getPosition().getColumn());

        bs.handleMove(chosenMove);
        System.out.println("Board after machine move:");
        System.out.println(bs.getBoard());
    }
}
