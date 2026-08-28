package com.robot.arm.domain.model;

import java.util.List;

public record AutomatonResult(
        boolean isAccepted,
        String message,
        List<RobotState> trace
) {
    public static AutomatonResult accepted(List<RobotState> trace) {
        return new AutomatonResult(true, "La cadena pertenece al autómata.", trace);
    }

    public static AutomatonResult rejected() {
        return new AutomatonResult(false, "La cadena no pertenece al autómata.", List.of());
    }
}