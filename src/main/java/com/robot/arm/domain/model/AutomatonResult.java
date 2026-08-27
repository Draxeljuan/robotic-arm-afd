package com.robot.arm.domain.model;

import java.util.List;

/**
 * Resultado formal de procesar una cadena de entrada w sobre el autómata M.
 *
 * - accepted = true: w ∈ Σ* fue reconocida; trace contiene la secuencia completa
 *   de estados q0, q1, ..., qn recorridos (incluye el estado inicial).
 * - accepted = false: algún símbolo de w no pertenece a Σ; trace está vacío y
 *   rejectionReason explica el motivo (se usa únicamente para mostrar
 *   "la cadena no pertenece").
 */
public record AutomatonResult(boolean accepted, List<RobotState> trace, String rejectionReason) {

    public static AutomatonResult accepted(List<RobotState> trace) {
        return new AutomatonResult(true, trace, null);
    }

    public static AutomatonResult rejected(String reason) {
        return new AutomatonResult(false, List.of(), reason);
    }
}
