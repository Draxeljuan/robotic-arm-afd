package com.robot.arm.domain.model;

public record RobotState(
        int x,
        int y,
        GripperState gripperState,
        PiecePosition piece1,
        PiecePosition piece2,
        AutomatonStateNode activeNode // Ahora el nodo del grafo es explícito
) {
    public static RobotState initial() {
        return new RobotState(
                0, 0,
                GripperState.EMPTY,
                PiecePosition.onGrid(1, 1),
                PiecePosition.onGrid(2, 2),
                AutomatonStateNode.Q0_INITIAL
        );
    }
}