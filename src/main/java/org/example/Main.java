package org.example;

import org.example.model.Board;
import org.example.model.Position;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Position> xPosition = new ArrayList<>();
        List<Position> oPosition = new ArrayList<>();
        xPosition.add(new Position(0, 0));
        xPosition.add(new Position(14, 14));
        oPosition.add(new Position(3, 3));

        Board board = new Board(xPosition, oPosition, true);
        System.out.println(board);
    }
}