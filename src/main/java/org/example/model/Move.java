package org.example.model;

import java.util.Objects;

/**
 * Movement of the tokens
 */
public class Move {
    private final Players mover;
    private final Position position;

    public Move(Players p_mover, Position p_position) {
        mover = p_mover;
        position = p_position;
    }

    public Players getMover() {
        return mover;
    }
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Move)) return false;
        Move move = (Move) o;
        return Objects.equals(position, move.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    @Override
    public String toString() {
        return "Move{" + "position=" + position + ", mover=" + mover + '}';
    }
}
