package org.example.model;

import java.util.List;

/**
 * X player
 */
public class XPlayer implements Players {
    private List<Position> positions;

    public XPlayer(List<Position> positions) {
        this.positions = positions;
    }

    @Override
    public List<Position> getPositions() {
        return positions;
    }

    public void move(Position newPosition) {
        positions.add(newPosition);
    }

    @Override
    public String toString() {
        return positions.toString();
    }
}
