package org.example.command;

import org.example.database.DbInit;
import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.model.Position;
import org.example.service.BoardService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command to save the board after playing
 */
public class SaveCommand extends Command {

    private static final String REGEX = "^SAVE(?:\\s+(\\S+))?$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.EDIT,
            GamePhase.HAVE_BOARD,
            GamePhase.HAVE_SIDE,
            GamePhase.PLAYING,
            GamePhase.GAME_ENDED
    );

    public SaveCommand() {
        super("SAVE", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {

        BoardService service = state.getBoardService();
        if(service == null) {
            System.out.println("Error: cannot save, No board exists yet.");
            return;
        }

        String name = null;
        try { name = matcher.group(1); } catch(Exception ignored) {}

        if(name == null || name.isBlank()) {
            System.out.println("No name given. Save skipped.");
            return;
        }

        Board board = service.getBoard();

        StringBuilder sb = new StringBuilder();
        for(int r = 0; r < 15; r++) {
            for(int c = 0; c < 15; c++) {
                Position p = new Position(r, c);
                if(board.getXPosition() != null && board.getXPosition().contains(p)) sb.append("X");
                else if(board.getOPosition() != null && board.getOPosition().contains(p)) sb.append("O");
                else sb.append(" ");
            }
        }

        String tableDesc = sb.toString();
        String query = """
                INSERT INTO SavedGameGomoku (NAME, TABLEDESCRIPTION, IS_X_TURN, IS_HUMAN_PLAYS_X)
                VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "");
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, name);
            pst.setString(2, tableDesc);
            pst.setInt(3, board.isXTurn() ? 1 : 0);
            pst.setInt(4, state.isHumanPlaysX() ? 1 : 0);

            pst.executeUpdate();
            System.out.println("Game saved successfully as " + name);
        } catch(SQLException ex) {
            System.out.println("Error: saving to DB failed: " + ex.getMessage());
            return;
        }

        // Phase transition
        if(state.getGamePhase() == GamePhase.GAME_ENDED) {
            state.setGamePhase(GamePhase.SAVED_GAME);
            System.out.println("Game ended. Save completed. Phase changed to SAVED_GAME.");
        } else {
            System.out.println("Save completed (Phase unchanged).");
        }
    }
}
