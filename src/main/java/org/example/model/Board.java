package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Creating board
 */
public class Board {
    private final List<Position> xPosition;
    private final List<Position> oPosition;
    private final boolean isXTurn;

    public Board(List<Position> xPosition , List<Position> oPosition, boolean isXTurn) {
        this.xPosition = (xPosition != null) ? xPosition : new ArrayList<>();
        this.oPosition = (oPosition != null) ? oPosition : new ArrayList<>();
        this.isXTurn = isXTurn;
    }

    public List<Position> getXPosition() {
        return xPosition;
    }
    public List<Position> getOPosition() {
        return oPosition;
    }
    public boolean isXTurn() {
        return isXTurn;
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        board.append("    1  2  3  4  5  6  7  8  9  10 11 12 13 14 15\n");
        for(int row = 0; row < 15; row++) {
            board.append(row + 1 + ((row < 9) ? "   " : "  "));
            for(int col = 0; col < 15; col++) {
                Position position = new Position(row, col);

                if(xPosition != null && xPosition.contains(position)) {
                    board.append("X  ");
                }
                else if(oPosition != null && oPosition.contains(position)) {
                    board.append("O  ");
                }
                else {
                    board.append(".  ");
                }
            }
            board.append(" " + (row + 1) + "\n");
        }
        board.append("    1  2  3  4  5  6  7  8  9  10 11 12 13 14 15\n");
        return board.toString();
    }
}
