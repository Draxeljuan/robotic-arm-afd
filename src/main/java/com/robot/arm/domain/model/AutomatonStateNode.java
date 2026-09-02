package com.robot.arm.domain.model;

public enum AutomatonStateNode {
    Q0_INITIAL("q0: Inicio"),
    Q1_MOVING("q1: Movimiento"),
    Q2_HOLDING("q2: Con Pieza"),
    Q3_RELEASED("q3: Garra Vacía");

    private final String label;

    AutomatonStateNode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
