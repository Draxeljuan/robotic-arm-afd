package com.robot.arm;

import com.robot.arm.domain.service.TransitionEngine;
import com.robot.arm.infrastructure.gui.RobotArmFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TransitionEngine engine = new TransitionEngine();
            RobotArmFrame frame = new RobotArmFrame(engine);
            frame.setVisible(true);
        });
    }
}