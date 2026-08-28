package com.robot.arm.infrastructure.gui;

import com.robot.arm.domain.model.AutomatonResult;
import com.robot.arm.domain.model.RobotState;
import com.robot.arm.domain.ports.in.ProcessSequenceUseCase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RobotArmFrame extends JFrame {
    private final ProcessSequenceUseCase processSequenceUseCase;
    private final GridPanel gridPanel;
    private final JTextField inputField;
    private final JLabel statusLabel;
    private final JLabel stepLabel;
    private Timer animationTimer;
    private List<RobotState> currentTrace;
    private int animationIndex;

    public RobotArmFrame(ProcessSequenceUseCase processSequenceUseCase) {
        this.processSequenceUseCase = processSequenceUseCase;

        setTitle("AFD - Control de Brazo Robótico (2 Piezas)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gridPanel = new GridPanel();
        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        controlPanel.setBackground(new Color(238, 240, 245));

        JLabel titleLabel = new JLabel("Ingrese la secuencia de comandos (Σ = {U, D, L, R, +, -}):");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        inputField = new JTextField(25);
        inputField.setFont(new Font("Monospaced", Font.BOLD, 16));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // Ejecución al presionar ENTER
        inputField.addActionListener(e -> onExecuteSequence());

        statusLabel = new JLabel("Estado: Esperando comando (Presione ENTER)");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        statusLabel.setForeground(new Color(44, 62, 80));

        stepLabel = new JLabel("Paso: 0 / 0");
        stepLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        controlPanel.add(titleLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPanel.add(inputField);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        controlPanel.add(stepLabel);

        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void onExecuteSequence() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        String input = inputField.getText().trim();
        AutomatonResult result = processSequenceUseCase.process(input);

        if (!result.isAccepted()) {
            statusLabel.setText("La cadena no pertenece al autómata.");
            statusLabel.setForeground(new Color(192, 57, 43));
            stepLabel.setText("Paso: -");
            gridPanel.updateState(RobotState.initial());
            return;
        }

        statusLabel.setText("Cadena ACEPTADA. Ejecutando animación paso a paso...");
        statusLabel.setForeground(new Color(39, 174, 96));

        currentTrace = result.trace();
        animationIndex = 0;

        animationTimer = new Timer(450, (ActionEvent e) -> {
            if (animationIndex < currentTrace.size()) {
                RobotState state = currentTrace.get(animationIndex);
                gridPanel.updateState(state);
                stepLabel.setText("Paso " + animationIndex + " / " + (currentTrace.size() - 1));
                animationIndex++;
            } else {
                animationTimer.stop();
                statusLabel.setText("Secuencia completada con éxito.");
            }
        });
        animationTimer.start();
    }
}