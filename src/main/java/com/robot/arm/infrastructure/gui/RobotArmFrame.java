package com.robot.arm.infrastructure.gui;

import com.robot.arm.domain.model.AutomatonResult;
import com.robot.arm.domain.model.RobotState;
import com.robot.arm.domain.ports.in.ProcessSequenceUseCase;
import com.robot.arm.domain.service.TransitionEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Adaptador de entrada/salida (GUI) sin botones y con tipografías ampliadas.
 */
public class RobotArmFrame extends JFrame {

    private static final int STEP_DELAY_MS = 700;

    private final ProcessSequenceUseCase engine = new TransitionEngine();
    private final GridPanel gridPanel = new GridPanel();
    private final JTextField inputField = new JTextField(25);
    private final JLabel statusLabel = new JLabel("Ingrese una cadena sobre Σ = {U, D, L, R, +, -} y presione Enter");
    private final JLabel stepLabel = new JLabel(" ");

    private Timer animationTimer;
    private List<RobotState> currentTrace;
    private int stepIndex;

    public RobotArmFrame() {
        super("AFD - Brazo Robótico 3x3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        rootPanel.setBackground(new Color(240, 242, 245));
        setContentPane(rootPanel);

        // --- Panel Superior ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        top.setOpaque(false);

        JLabel promptLabel = new JLabel("Cadena de entrada:");
        promptLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Texto más grande
        promptLabel.setForeground(new Color(33, 37, 41));

        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); // Campo de texto más cómodo
        inputField.setPreferredSize(new Dimension(280, 35));

        top.add(promptLabel);
        top.add(inputField);
        rootPanel.add(top, BorderLayout.NORTH);

        // --- Panel Central ---
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(gridPanel);
        rootPanel.add(centerContainer, BorderLayout.CENTER);

        // --- Panel Inferior ---
        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 6));
        bottom.setOpaque(false);

        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Estados más legibles
        statusLabel.setForeground(new Color(73, 80, 87));

        stepLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stepLabel.setForeground(new Color(108, 117, 125));

        bottom.add(statusLabel);
        bottom.add(stepLabel);
        rootPanel.add(bottom, BorderLayout.SOUTH);

        // Evento exclusivo por Enter
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEnterPressed();
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void onEnterPressed() {
        stopAnimationIfRunning();

        String raw = inputField.getText().trim();
        AutomatonResult result = engine.process(raw);

        if (!result.accepted()) {
            statusLabel.setText("La cadena no pertenece al lenguaje.");
            statusLabel.setForeground(new Color(220, 53, 69));
            stepLabel.setText(" ");
            gridPanel.setState(TransitionEngine.initialState());
            return;
        }

        statusLabel.setForeground(new Color(73, 80, 87));
        currentTrace = result.trace();
        stepIndex = 0;
        gridPanel.setState(currentTrace.getFirst());
        statusLabel.setText("Procesando cadena paso a paso...");
        stepLabel.setText("Paso 0 / " + (currentTrace.size() - 1) + " — Estado inicial q0");

        animationTimer = new Timer(STEP_DELAY_MS, this::advanceStep);
        animationTimer.setInitialDelay(STEP_DELAY_MS);
        animationTimer.start();
    }

    private void advanceStep(ActionEvent e) {
        stepIndex++;
        if (currentTrace == null || stepIndex >= currentTrace.size()) {
            stopAnimationIfRunning();
            statusLabel.setText("Cadena aceptada. Secuencia completa.");
            statusLabel.setForeground(new Color(25, 135, 84));
            return;
        }
        RobotState s = currentTrace.get(stepIndex);
        gridPanel.setState(s);
        stepLabel.setText("Paso " + stepIndex + " / " + (currentTrace.size() - 1)
                + " — Posición (" + s.gripperX() + "," + s.gripperY() + "), "
                + (s.holding() ? "garra llena" : "garra vacía"));
    }

    private void stopAnimationIfRunning() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
}