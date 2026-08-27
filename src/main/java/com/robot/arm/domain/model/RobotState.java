package com.robot.arm.domain.model;

import java.util.Optional;

/**
 * Estado q = (x, y, holding, piecePosition) del AFD.
 *
 * - gripperX, gripperY: posición actual de la garra en la cuadrícula 3x3.
 * - holding: true si la garra sostiene la única pieza del tablero.
 * - piecePosition: presente (Optional con valor) cuando la pieza está sobre el
 *   tablero; vacío (Optional.empty()) cuando la pieza está siendo sostenida
 *   por la garra (holding = true). holding y piecePosition son consistentes
 *   entre sí por construcción: holding == piecePosition.isEmpty().
 */
public record RobotState(int gripperX, int gripperY, boolean holding, Optional<Position> piecePosition) {

    public RobotState {
        if (holding != piecePosition.isEmpty()) {
            throw new IllegalArgumentException(
                    "Estado inconsistente: holding debe ser verdadero si y solo si piecePosition está vacío");
        }
    }
}
