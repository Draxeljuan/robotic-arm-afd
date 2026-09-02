package com.robot.arm.domain.model;

public record Position(int x, int y) {
    public Position {
        if (x < 0 || x > 2 || y < 0 || y > 2) {
            throw new IllegalArgumentException("Coordenadas fuera del rango 3x3: (" + x + "," + y + ")");
        }
    }

    public static Position of(int x, int y) {
        return new Position(x, y);
    }
}
