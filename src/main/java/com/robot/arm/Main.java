package com.robot.arm;

import com.robot.arm.infrastructure.gui.RobotArmFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RobotArmFrame().setVisible(true));
    }
}
