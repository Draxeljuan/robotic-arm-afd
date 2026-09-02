package com.robot.arm.domain.model;

public record PiecePosition(Position gridPosition, boolean isHeld) {
    public static PiecePosition onGrid(int x, int y) {
        return new PiecePosition(Position.of(x, y), false);
    }

    public static PiecePosition held() {
        return new PiecePosition(null, true);
    }

    public boolean isAt(int x, int y) {
        return !isHeld && gridPosition != null && gridPosition.x() == x && gridPosition.y() == y;
    }
}
