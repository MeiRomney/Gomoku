package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * O player
 */
public class OPlayer implements Players{
    public List<Position> positions;

    public OPlayer(List<Position> positions) {
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
