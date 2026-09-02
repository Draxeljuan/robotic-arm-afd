package com.robot.arm.domain.service;

import com.robot.arm.domain.model.*;
import com.robot.arm.domain.ports.in.ProcessSequenceUseCase;
import java.util.ArrayList;
import java.util.List;

public class TransitionEngine implements ProcessSequenceUseCase {

    @Override
    public AutomatonResult process(String sequence) {
        if (sequence == null) return AutomatonResult.rejected();

        RobotState current = RobotState.initial();
        List<RobotState> trace = new ArrayList<>();
        trace.add(current);

        for (char c : sequence.trim().toCharArray()) {
            var symbolOpt = Symbol.fromChar(c);
            if (symbolOpt.isEmpty()) return AutomatonResult.rejected();

            current = delta(current, symbolOpt.get());
            trace.add(current);
        }
        return AutomatonResult.accepted(trace);
    }

    public RobotState delta(RobotState state, Symbol symbol) {
        int x = state.x();
        int y = state.y();
        GripperState gripper = state.gripperState();
        PiecePosition p1 = state.piece1();
        PiecePosition p2 = state.piece2();

        return switch (symbol) {
            // Cualquier movimiento cambia el estado del grafo a q1
            case U -> new RobotState(x, Math.min(2, y + 1), gripper, p1, p2, AutomatonStateNode.Q1_MOVING);
            case D -> new RobotState(x, Math.max(0, y - 1), gripper, p1, p2, AutomatonStateNode.Q1_MOVING);
            case L -> new RobotState(Math.max(0, x - 1), y, gripper, p1, p2, AutomatonStateNode.Q1_MOVING);
            case R -> new RobotState(Math.min(2, x + 1), y, gripper, p1, p2, AutomatonStateNode.Q1_MOVING);

            case PLUS -> handleGrab(state, x, y, gripper, p1, p2);
            case MINUS -> handleDrop(state, x, y, gripper, p1, p2);
        };
    }

    private RobotState handleGrab(RobotState state, int x, int y, GripperState gripper, PiecePosition p1, PiecePosition p2) {
        if (gripper != GripperState.EMPTY) {
            // Ya tiene algo y agarra -> Se mantiene en Q2
            return new RobotState(x, y, gripper, p1, p2, AutomatonStateNode.Q2_HOLDING);
        }
        if (p1.isAt(x, y)) {
            return new RobotState(x, y, GripperState.HOLDING_PIECE_1, PiecePosition.held(), p2, AutomatonStateNode.Q2_HOLDING);
        } else if (p2.isAt(x, y)) {
            return new RobotState(x, y, GripperState.HOLDING_PIECE_2, p1, PiecePosition.held(), AutomatonStateNode.Q2_HOLDING);
        } else {
            // Garra vacía intenta agarrar en la nada -> Q3
            return new RobotState(x, y, GripperState.EMPTY, p1, p2, AutomatonStateNode.Q3_RELEASED);
        }
    }

    private RobotState handleDrop(RobotState state, int x, int y, GripperState gripper, PiecePosition p1, PiecePosition p2) {
        if (gripper == GripperState.EMPTY) {
            // Intenta soltar algo y no tiene nada -> Q3
            return new RobotState(x, y, GripperState.EMPTY, p1, p2, AutomatonStateNode.Q3_RELEASED);
        }
        if (gripper == GripperState.HOLDING_PIECE_1) {
            if (p2.isAt(x, y)) {
                // Intenta soltar sobre otra pieza -> Se mantiene en Q2
                return new RobotState(x, y, gripper, p1, p2, AutomatonStateNode.Q2_HOLDING);
            }
            return new RobotState(x, y, GripperState.EMPTY, PiecePosition.onGrid(x, y), p2, AutomatonStateNode.Q3_RELEASED);
        }
        if (gripper == GripperState.HOLDING_PIECE_2) {
            if (p1.isAt(x, y)) {
                // Intenta soltar sobre otra pieza -> Se mantiene en Q2
                return new RobotState(x, y, gripper, p1, p2, AutomatonStateNode.Q2_HOLDING);
            }
            return new RobotState(x, y, GripperState.EMPTY, p1, PiecePosition.onGrid(x, y), AutomatonStateNode.Q3_RELEASED);
        }
        return state;
    }
}