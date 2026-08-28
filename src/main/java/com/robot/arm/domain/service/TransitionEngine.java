package com.robot.arm.domain.service;

import com.robot.arm.domain.model.*;
import com.robot.arm.domain.ports.in.ProcessSequenceUseCase;

import java.util.ArrayList;
import java.util.List;

public class TransitionEngine implements ProcessSequenceUseCase {

    @Override
    public AutomatonResult process(String sequence) {
        if (sequence == null) {
            return AutomatonResult.rejected();
        }

        RobotState current = RobotState.initial();
        List<RobotState> trace = new ArrayList<>();
        trace.add(current);

        for (char c : sequence.trim().toCharArray()) {
            var symbolOpt = Symbol.fromChar(c);
            if (symbolOpt.isEmpty()) {
                // Único caso de rechazo de cadena: Símbolo fuera del alfabeto Σ
                return AutomatonResult.rejected();
            }

            Symbol symbol = symbolOpt.get();
            current = delta(current, symbol);
            trace.add(current);
        }

        return AutomatonResult.accepted(trace);
    }

    /**
     * Función de Transición δ(q, σ) -> q'
     * Implementa autobucle (mantiene el estado) ante cualquier operación no permitida.
     */
    public RobotState delta(RobotState state, Symbol symbol) {
        int x = state.x();
        int y = state.y();
        GripperState gripper = state.gripperState();
        PiecePosition p1 = state.piece1();
        PiecePosition p2 = state.piece2();

        return switch (symbol) {
            // Movimientos con acotamiento/rebote en los bordes 0..2 (Autobucle en bordes)
            case U -> new RobotState(x, Math.min(2, y + 1), gripper, p1, p2, false);
            case D -> new RobotState(x, Math.max(0, y - 1), gripper, p1, p2, false);
            case L -> new RobotState(Math.max(0, x - 1), y, gripper, p1, p2, false);
            case R -> new RobotState(Math.min(2, x + 1), y, gripper, p1, p2, false);

            case PLUS -> handleGrab(state, x, y, gripper, p1, p2);
            case MINUS -> handleDrop(state, x, y, gripper, p1, p2);
        };
    }

    private RobotState handleGrab(RobotState currentState, int x, int y, GripperState gripper, PiecePosition p1, PiecePosition p2) {
        // Si ya sostiene una pieza, mantiene el estado (Autobucle)
        if (gripper != GripperState.EMPTY) {
            return currentState;
        }

        // Si la garra está vacía y sobre una pieza, la toma
        if (p1.isAt(x, y)) {
            return new RobotState(x, y, GripperState.HOLDING_PIECE_1, PiecePosition.held(), p2, false);
        } else if (p2.isAt(x, y)) {
            return new RobotState(x, y, GripperState.HOLDING_PIECE_2, p1, PiecePosition.held(), false);
        } else {
            // Si intenta tomar en casilla vacía, mantiene el estado (Autobucle)
            return currentState;
        }
    }

    private RobotState handleDrop(RobotState currentState, int x, int y, GripperState gripper, PiecePosition p1, PiecePosition p2) {
        // Si la garra está vacía, no hace nada y mantiene el estado (Autobucle)
        if (gripper == GripperState.EMPTY) {
            return currentState;
        }

        // Sosteniendo Pieza 1:
        if (gripper == GripperState.HOLDING_PIECE_1) {
            // Si intenta soltar en casilla ocupada por Pieza 2 -> MANTIENE EL ESTADO (Autobucle, no la suelta)
            if (p2.isAt(x, y)) {
                return currentState;
            }
            // Casilla libre -> Deposita Pieza 1
            return new RobotState(x, y, GripperState.EMPTY, PiecePosition.onGrid(x, y), p2, false);
        }

        // Sosteniendo Pieza 2:
        if (gripper == GripperState.HOLDING_PIECE_2) {
            // Si intenta soltar en casilla ocupada por Pieza 1 -> MANTIENE EL ESTADO (Autobucle, no la suelta)
            if (p1.isAt(x, y)) {
                return currentState;
            }
            // Casilla libre -> Deposita Pieza 2
            return new RobotState(x, y, GripperState.EMPTY, p1, PiecePosition.onGrid(x, y), false);
        }

        return currentState;
    }
}