package org.example.model;

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
    public String toString() {
        return "Move{" + "position=" + position + ", mover=" + mover + '}';
    }
}
