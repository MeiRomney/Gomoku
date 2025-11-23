package org.example.ui;

import org.example.database.DbInit;
import org.example.model.*;
import org.example.service.BoardService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class PlayOnTerminal {
    private static final Logger logger = Logger.getLogger(PlayOnTerminal.class.getName());

    private final GameState state = new GameState();
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        DbInit.initDb();
        state.setLoadedAGame(questionForLoading());
        if(!state.isLoadedAGame()) {
            editing();
        }
        System.out.println("The board to play is: \n" + state.getBoardService().getBoard());
        readInSide();
        playing();
        saveThePlayedGame();
        DbInit.stopWebServer();
    }

    private boolean questionForLoading() {
        System.out.println("Do you want to load a saved game from the database? ");
        char answer = ' ';
        while(!(answer == 'y' || answer == 'n')) {
            System.out.print("Give 'y' or 'n' as answer: \n");
            String read =scanner.nextLine();
            answer = read.toLowerCase().charAt(0);
        }
        if(answer == 'n') return false;

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            logger.severe("H2 database not found.");
        }
        loadAGame();
        return true;
    }

    private void loadAGame() {
        String queryAll = "SELECT * FROM SavedGameGomoku ORDER BY ID;";
        String queryOne = "SELECT NAME, TABLEDESCRIPTION, IS_X_TURN, IS_HUMAN_PLAYS_X FROM SavedGameGomoku WHERE ID = ?;";

        try (Connection connection = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "")) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(queryAll);
            int maxId = -1;
            System.out.println("Saved games:");
            while(rs.next()) {
                int id = rs.getInt("ID");
                maxId = Math.max(maxId, id);
                String name = rs.getString("NAME");
                System.out.println(id + ": " + name);
            }
            rs.close();
            stmt.close();

            int selectedId = -1;
            while(selectedId < 1 || selectedId > maxId) {
                System.out.println("Select an existing id (1-" + maxId + "): ");
                try {
                    selectedId = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException ex) {
                    logger.warning("Invalid number format");
                }
            }

            PreparedStatement ps = connection.prepareStatement(queryOne);
            ps.setInt(1, selectedId);
            ResultSet rs2 = ps.executeQuery();
            if(rs2.next()) {
                boolean isHumanPlaysX = rs2.getInt("IS_HUMAN_PLAYS_X") == 1;
                boolean isXTurn = rs2.getInt("IS_X_TURN") == 1;
                String desc = rs2.getString("TABLEDESCRIPTION");

                List<Position> xPositions = new ArrayList<>();
                List<Position> oPositions = new ArrayList<>();
                for(int r = 0; r < 15; r++) {
                    for(int c = 0; c < 15; c++) {
                        int idx = r * 15 + c;
                        int ch = idx < desc.length() ? desc.charAt(idx) : '.';
                        //char ch = desc.charAt(r * 15 + c);
                        if(ch == 'X') xPositions.add(new Position(r, c));
                        else if(ch == 'O') oPositions.add(new Position(r, c));
                    }
                }
                state.setBoardService(new BoardService(new Board(xPositions, oPositions, isXTurn)));
                state.setHumanPlaysX(isHumanPlaysX);
                state.setXTurn(isXTurn);
                state.setLoadedAGame(true);
            }
            rs2.close();
            ps.close();
        } catch(SQLException ex) {
            logger.severe("Failed to load a saved game: " + ex.getMessage());
        }
    }

    private void readInSide() {
        System.out.println("Do you want to play as X or O? exit: q");
        while(true) {
            char c = scanner.nextLine().charAt(0);
            switch(Character.toLowerCase(c)) {
                case 'x' -> {
                    state.setHumanPlaysX(true);
                    System.out.println("You play X");
                    return;
                }
                case 'o' -> {
                    state.setHumanPlaysX(false);
                    System.out.println("You play O");
                    return;
                }
                case 'q' -> System.exit(0);
            }
            System.out.print("Try again (x/o/q): ");
        }
    }

    private void editing() {
        List<Position> xPositions = new ArrayList<>();
        List<Position> oPositions = new ArrayList<>();
        System.out.println("Editing empty board");
        state.setBoardService(new BoardService(new Board(xPositions, oPositions, true)));
        state.setHumanPlaysX(true);
    }

    private void playing() {
        System.out.println("Starting board: \n" + state.getBoardService().getBoard());
        if(state.isHumanPlaysX()) playWithX();
        else playWithO();
    }

    private void playWithX() {
        while(true) {
            BoardService bs = state.getBoardService();
            if(bs.xWins()) {
                System.out.println("You win!");
                return;
            }
            if(bs.oWins()) {
                System.out.println("You lose!");
                return;
            }

            if(state.isXTurn()) {
                List<Move> possible = bs.determineXPossibleMoves();
                while(true) {
                    System.out.println("Your move X (row(1-15), col(1-15)), exit(0): ");
                    int r = scanner.nextInt();
                    int c = scanner.nextInt();
                    scanner.nextLine();

                    if(r == 0 || c == 0) System.exit(0);
                    if(r < 1 || r > 15 || c < 1 || c > 15) {
                        System.out.println("Invalid coordinates. Try again.");
                        continue;
                    }

                    Position pos = new Position(r -1, c - 1);
                    Move move = new Move(new XPlayer(bs.getBoard().getXPosition()), pos);

                    if(!possible.contains(move)) {
                        System.out.println("Illegal move. The tokens have to be adjacent.");
                        continue;
                    }
                    if (bs.getBoard().getXPosition().contains(pos) ||
                            bs.getBoard().getOPosition().contains(pos)) {
                        System.out.println("Tile occupied. Try again.");
                        continue;
                    }


                    bs.handleMove(move);
                    break;
                }
                state.setXTurn(false);
            } else {
                bs.randomOMove();
                state.setXTurn(true);
            }
            System.out.println("Board:\n" + bs.getBoard());
        }
    }

    private void playWithO() {
        while(true) {
            BoardService bs = state.getBoardService();
            if(bs.xWins()) {
                System.out.println("You lose!");
                return;
            }
            if(bs.oWins()) {
                System.out.println("You win!");
                return;
            }

            if(!state.isXTurn()) {
                List<Move> possible = bs.determineOPossibleMoves();
                while(true) {
                    System.out.println("Your move O (row(1-15), col(1-15)), exit(0): ");
                    int r = scanner.nextInt();
                    int c = scanner.nextInt();
                    scanner.nextLine();

                    if(r == 0 || c == 0) System.exit(0);
                    if(r < 1 || r > 15 || c < 1 || c > 15) {
                        System.out.println("Invalid coordinates. Try again.");
                        continue;
                    }

                    Position pos = new Position(r -1, c - 1);
                    Move move = new Move(new OPlayer(bs.getBoard().getOPosition()), pos);

                    if(!possible.contains(move)) {
                        System.out.println("Illegal move. The tokens have to be adjacent.");
                        continue;
                    }
                    if (bs.getBoard().getXPosition().contains(pos) ||
                            bs.getBoard().getOPosition().contains(pos)) {
                        System.out.println("Tile occupied. Try again.");
                        continue;
                    }

                    bs.handleMove(move);
                    break;
                }

                state.setXTurn(true);
            } else {
                bs.randomXMove();
                state.setXTurn(false);
            }
            System.out.println("Board:\n" + bs.getBoard());
        }
    }

    private void saveThePlayedGame() {
        System.out.print("Save game? (enter name to save or leave blank to skip): ");
        String name = scanner.nextLine();
        if(name == null || name.isBlank()) {
            System.out.println("No save.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        Board b = state.getBoardService().getBoard();
        for(int r = 0; r < 15; r++) {
            for(int c = 0; c < 15; c++) {
                Position p = new Position(r, c);
                if(b.getXPosition() != null && b.getXPosition().contains(p)) sb.append('X');
                else if(b.getOPosition() != null && b.getOPosition().contains(p)) sb.append('O');
                else sb.append('.');
            }
        }
        String desc = sb.toString();
        String query = "INSERT INTO SavedGameGomoku (NAME, TABLEDESCRIPTION, IS_X_TURN, IS_HUMAN_PLAYS_X) " +
                        "VALUES (?, ?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(DbInit.getInMemoryUrl(), "sa", "")) {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setInt(3, state.isXTurn() ? 1 : 0);
            stmt.setInt(4, state.isHumanPlaysX() ? 1 : 0);

            stmt.executeUpdate();
            System.out.println("Game saved as " + name);
        } catch (SQLException e) {
            logger.severe("Save failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new PlayOnTerminal().run();
    }
}
