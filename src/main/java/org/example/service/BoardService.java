package org.example.service;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardService {
    public static final int BOARD_SIZE = 15;
    public static final int WIN_LENGTH = 5;

    private Board board;
    private final Random random = new Random();

    public BoardService(Board board) {
        this.board = board;
    }
    public Board getBoard() {
        return board;
    }

    public boolean handleMove(Move move) {
        Players player = move.getMover();
        Position position = move.getPosition();

        if(board.getXPosition().contains(position) || board.getOPosition().contains(position)) {
            return false;
        }

        if(player instanceof XPlayer) {
            board.getXPosition().add(position);
        } else if(player instanceof OPlayer) {
            board.getOPosition().add(position);
        }
        return true;
    }

    // Determine possible
    public List<Move> determinePossibleMoves(Players player) {
        List<Move> moves = new ArrayList<>();

        boolean boardEmpty = board.getXPosition().isEmpty() && board.getOPosition().isEmpty();

        for(int r = 0; r < BOARD_SIZE; r++) {
            for(int c = 0; c < BOARD_SIZE; c++) {
                Position position = new Position(r, c);
                if(board.getXPosition().contains(position)) continue;
                if(board.getOPosition().contains(position)) continue;
                if(!boardEmpty && !isAdjacentToToken(position)) continue;

                moves.add(new Move(player, position));
            }
        }
        return moves;
    }

    public List<Move> determineXPossibleMoves() {
        return determinePossibleMoves(new XPlayer(board.getXPosition()));
    }

    public List<Move> determineOPossibleMoves() {
        return determinePossibleMoves(new OPlayer(board.getOPosition()));
    }

    // Random moves
    public void randomXMove() {
        List<Move> moves = determineXPossibleMoves();
        if(!moves.isEmpty()) {
            Move move = moves.get(random.nextInt(moves.size()));
            handleMove(move);
        }
    }

    public void randomOMove() {
        List<Move> moves = determineOPossibleMoves();
        if(!moves.isEmpty()) {
            Move move = moves.get(random.nextInt(moves.size()));
            handleMove(move);
        }
    }

    // Check winner
    public boolean xWins() {
        return hasFiveInRow(board.getXPosition());
    }

    public boolean oWins() {
        return hasFiveInRow(board.getOPosition());
    }



    // Helpers
    private boolean hasFiveInRow(List<Position> positions) {
        boolean[][] grid = new boolean[BOARD_SIZE][BOARD_SIZE];

        // Mark the grid
        for(Position p : positions) {
            grid[p.getRow()][p.getColumn()] = true;
        }

        // Check every cell
        for(int r = 0; r < BOARD_SIZE; r++) {
            for(int c = 0; c < BOARD_SIZE; c++) {
                if(!grid[r][c]) continue;

                // 4 directions: right, down, down-right, up-right
                if(checkDirection(grid, r, c, 0, 1)) return true;
                if(checkDirection(grid, r, c, 1, 0)) return true;
                if(checkDirection(grid, r, c, 1, 1)) return true;
                if(checkDirection(grid, r, c, -1, 1)) return true;
            }
        }
        return false;
    }

    private boolean checkDirection(boolean[][] grid, int r, int c, int dr, int dc) {
        for(int i = 1; i < WIN_LENGTH; i++)  {
            int nr = r + dr * i;
            int nc = c + dc * i;

            if(nr < 0 || nr >= BOARD_SIZE || nc < 0 || nc >= BOARD_SIZE) return false;
            if(!grid[nr][nc]) return false;
        }
        return true;
    }

    private boolean isAdjacentToToken(Position p) {
        // Check all X position
        for(Position x : board.getXPosition()) {
            if(Math.abs(x.getRow() - p.getRow()) <= 1 &&
            Math.abs(x.getColumn() - p.getColumn()) <= 1) {
                return true;
            }
        }
        // Check all O position
        for(Position o : board.getOPosition()) {
            if(Math.abs(o.getRow() - p.getRow()) <= 1 &&
                    Math.abs(o.getColumn() - p.getColumn()) <= 1) {
                return true;
            }
        }
        return false;
    }

    public static Board getEmptyBoard() {
        return new Board (new ArrayList<>(), new ArrayList<>(), true);
    }
}
