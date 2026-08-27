package com.robot.arm.domain.service;

import com.robot.arm.domain.model.AutomatonResult;
import com.robot.arm.domain.model.Position;
import com.robot.arm.domain.model.RobotState;
import com.robot.arm.domain.model.Symbol;
import com.robot.arm.domain.ports.in.ProcessSequenceUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de δ para el AFD del brazo robótico 3x3 con una sola pieza.
 *
 * Responsabilidad única (SRP): esta clase solo calcula transiciones de estado;
 * no conoce nada de la interfaz gráfica.
 */
public final class TransitionEngine implements ProcessSequenceUseCase {

    /** Posición inicial de la única pieza sobre el tablero (celda (1,1), centro). */
    private static final Position INITIAL_PIECE_POSITION = new Position(1, 1);

    /**
     * q0 = (0, 0, holding=false, pieza en (1,1)).
     */
    public static RobotState initialState() {
        return new RobotState(0, 0, false, Optional.of(INITIAL_PIECE_POSITION));
    }

    @Override
    public AutomatonResult process(String input) {
        // Verificación de pertenencia al alfabeto: si algún símbolo no está en Σ,
        // la cadena completa no pertenece al lenguaje reconocido por M.
        for (char c : input.toCharArray()) {
            if (Symbol.fromChar(c).isEmpty()) {
                return AutomatonResult.rejected("La cadena no pertenece.");
            }
        }

        List<RobotState> trace = new ArrayList<>();
        RobotState current = initialState();
        trace.add(current);

        for (char c : input.toCharArray()) {
            Symbol symbol = Symbol.fromChar(c).orElseThrow();
            current = delta(current, symbol);
            trace.add(current);
        }

        return AutomatonResult.accepted(trace);
    }

    /**
     * δ(q, σ) — función de transición del autómata.
     */
    public RobotState delta(RobotState q, Symbol symbol) {
        return switch (symbol) {
            case UP -> new RobotState(q.gripperX(), Math.min(2, q.gripperY() + 1), q.holding(), q.piecePosition());
            case DOWN -> new RobotState(q.gripperX(), Math.max(0, q.gripperY() - 1), q.holding(), q.piecePosition());
            case LEFT -> new RobotState(Math.max(0, q.gripperX() - 1), q.gripperY(), q.holding(), q.piecePosition());
            case RIGHT -> new RobotState(Math.min(2, q.gripperX() + 1), q.gripperY(), q.holding(), q.piecePosition());
            case GRAB -> applyGrab(q);
            case DROP -> applyDrop(q);
        };
    }

    /**
     * Tomar (+): si la garra está vacía y la pieza está exactamente en (x,y),
     * la garra pasa a sostenerla. En cualquier otro caso (garra ya ocupada, o
     * no hay pieza en la casilla actual) es un autobucle: el estado no cambia
     * y la garra queda/permanece vacía.
     */
    private RobotState applyGrab(RobotState q) {
        if (q.holding()) {
            return q; // ya sostiene la pieza -> autobucle
        }
        Position piece = q.piecePosition().orElseThrow();
        boolean gripperOnPiece = piece.x() == q.gripperX() && piece.y() == q.gripperY();
        if (gripperOnPiece) {
            return new RobotState(q.gripperX(), q.gripperY(), true, Optional.empty());
        }
        return q; // no hay pieza aquí -> autobucle, la garra queda vacía
    }

    /**
     * Soltar (-): si la garra sostiene la pieza, esta se deposita en la
     * posición actual (x,y) — ya no se exige que la casilla esté libre, pues
     * solo existe una pieza en el tablero. Si la garra está vacía, es un
     * autobucle: el estado no cambia y la garra queda vacía.
     */
    private RobotState applyDrop(RobotState q) {
        if (!q.holding()) {
            return q; // no sostiene nada -> autobucle, la garra queda vacía
        }
        return new RobotState(q.gripperX(), q.gripperY(), false, Optional.of(new Position(q.gripperX(), q.gripperY())));
    }
}
