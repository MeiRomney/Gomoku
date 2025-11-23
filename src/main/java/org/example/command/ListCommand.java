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

public class ListCommand extends Command {
    private static final String REGEX = "^LIST(?:\\s+(\\d+))?$";

    private static final Set<GamePhase> ALLOWED = Set.of(
            GamePhase.EDIT,
            GamePhase.SAVED_GAME
    );

    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);

    public ListCommand() {
        super("LIST", REGEX, ALLOWED);
    }

    @Override
    public void apply(GameState state, Matcher matcher) {
        // If group(1) present => try to load that ID, otherwise list all saved games
        String idGroup = null;
        try {
            idGroup = matcher.group(1);
        } catch(Exception ignored) {
            // Empty
        }

        if(idGroup == null) {
            // List saved games
            System.out.println("Game saved in database:");
            System.out.println("------------------------------");
            String query = "SELECT ID, NAME FROM SavedGameGomoku ORDER BY ID";
            try(Connection conn = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

                boolean foundAny = false;
                while(rs.next()) {
                    foundAny = true;
                    int id = rs.getInt("ID");
                    String name = rs.getString("NAME");
                    System.out.printf("%3d  %s%n", id, name);
                }
                if(!foundAny) {
                    System.out.println("No saved game");
                }
            } catch (Exception e) {
                System.err.println("Error while listing saved games: " + e.getMessage());
            }
            System.out.println("------------------------------");
            // Phase unchanged
            return;
        }
        // If we get here an ID was provided, attempt to load it
        int idToLoad;
        try {
            idToLoad = Integer.parseInt(idGroup);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid id: " + idGroup);
            return;
        }

        final String queryById = "SELECT TABLEDESCRIPTION, IS_X_TURN, IS_HUMAN_PLAYS_X " +
                "FROM SavedGameGomoku WHERE ID = ?";

        try (Connection conn = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "");
             PreparedStatement pst = conn.prepareStatement(queryById)) {

            pst.setInt(1, idToLoad);
            try (ResultSet rs = pst.executeQuery()) {
                if(!rs.next()) {
                    System.out.printf("No saved game with ID %d%n", idToLoad);
                    return;
                }

                String tableDescription = rs.getString("TABLEDESCRIPTION");
                boolean isXTurn = rs.getInt("IS_X_TURN") == 1;
                boolean isHumanPlaysX = rs.getInt("IS_HUMAN_PLAYS_X") == 1;

                List<Position> xPositions = new ArrayList<>();
                List<Position> oPositions = new ArrayList<>();
                if(tableDescription != null) {
                    int max = Math.min(tableDescription.length(), 15*15);
                    for(int i = 0; i < max; i++) {
                        int row = i / 15;
                        int col = i % 15;
                        char ch = tableDescription.charAt(i);
                        if(ch == 'X') xPositions.add(new Position(row, col));
                        else if(ch == 'O') oPositions.add(new Position(row, col));
                    }
                }

                Board loadedBoard = new Board(xPositions, oPositions, isHumanPlaysX);
                state.setBoardService(new BoardService(loadedBoard));
                state.setLoadedAGame(true);
                state.setHumanPlaysX(isHumanPlaysX);
                state.setXTurn(isXTurn);
                // After loading, we change to HAVE_BOARD phase
                state.setGamePhase(GamePhase.HAVE_BOARD);

                System.out.printf("Loaded saved game ID %d.\n", idToLoad);
                System.out.println("Board:\n" + state.getBoardService().getBoard());
            }
        } catch (Exception e) {
            System.err.println("Error while loading saved games: " + e.getMessage());
        }
    }

    @Override
    public Matcher match(String input) {
        Matcher m = PATTERN.matcher(input.trim());
        return m.matches() ? m : null;
    }
}
