package org.example.command;

import org.example.database.DbInit;
import org.example.model.Board;
import org.example.model.GamePhase;
import org.example.model.GameState;
import org.example.model.Position;
import org.example.service.BoardService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Load class the user wants
 */
public class LoadCommand extends Command {

    private static final String REGEX = "^LOAD\\s+(\\d+)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.EDIT,
            GamePhase.HAVE_BOARD,
            GamePhase.HAVE_SIDE
    );

    public LoadCommand() {
        super("LOAD", REGEX, ALLOWED);
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input);
        return m.matches() ? m : null;
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        int id = Integer.parseInt(matcher.group(1));
        String sql = """
                SELECT TABLEDESCRIPTION, IS_X_TURN, IS_HUMAN_PLAYS_X
                FROM SavedGameGomoku
                WHERE ID = ?
                """;

        try (Connection conn = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                System.out.println("No saved game found with id: " + id);
                return;
            }

            boolean isXTurn = rs.getInt("IS_X_TURN") == 1;
            String table = rs.getString("TABLEDESCRIPTION");

            // Parse Position
            List<Position> xPositions = new ArrayList<>();
            List<Position> oPositions = new ArrayList<>();

            for(int r = 0; r < 15; r++) {
                for(int c = 0; c < 15; c++) {
                    int index = r * 15 + c;
                    char ch = (index < table.length()) ? table.charAt(index) : '.';
                    //char ch = table.charAt(r * 15 + c);
                    switch(ch) {
                        case 'X': xPositions.add(new Position(r, c)); break;
                        case 'O': oPositions.add(new Position(r, c)); break;
                        default: break;
                    }
                }
            }

            // Build the new board
            Board board = new Board(xPositions, oPositions, isXTurn);

            // Reset entire state
            state.setBoardService(new BoardService(board));
            state.setHumanPlaysX(false);
            state.setGamePhase(GamePhase.HAVE_BOARD);

            System.out.println("Loaded game with id: " + id);
            System.out.println("Board state:\n" + board);
        } catch (SQLException ex) {
            System.out.println("Database error while loading game: " + ex.getMessage());
        }
    }
}
