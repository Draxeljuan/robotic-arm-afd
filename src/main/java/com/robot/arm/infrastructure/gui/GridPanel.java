package com.robot.arm.infrastructure.gui;

import com.robot.arm.domain.model.Position;
import com.robot.arm.domain.model.RobotState;
import com.robot.arm.domain.service.TransitionEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Adaptador de salida (infraestructura): únicamente renderiza un RobotState.
 * Versión con celdas y textos más grandes.
 */
public class GridPanel extends JPanel {

    private static final int CELL = 150; // Celdas más grandes
    private static final int MARGIN = 45;

    private RobotState state;

    public GridPanel() {
        this.state = TransitionEngine.initialState();
        setPreferredSize(new Dimension(CELL * 3 + MARGIN * 2, CELL * 3 + MARGIN * 2));
        setBackground(new Color(248, 249, 250));
    }

    public void setState(RobotState state) {
        this.state = state;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cuadrícula
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int px = MARGIN + x * CELL;
                int py = MARGIN + (2 - y) * CELL;

                g2.setColor(Color.WHITE);
                g2.fillRect(px, py, CELL, CELL);

                g2.setColor(new Color(222, 226, 230));
                g2.drawRect(px, py, CELL, CELL);

                // Coordenadas con letra más grande
                g2.setColor(new Color(173, 181, 189));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.drawString("(" + x + "," + y + ")", px + 14, py + 24);
            }
        }

        // Pieza y garra
        state.piecePosition().ifPresent(p -> drawPiece(g2, p));
        drawGripper(g2, state.gripperX(), state.gripperY(), state.holding());
    }

    private void drawPiece(Graphics2D g2, Position p) {
        int px = MARGIN + p.x() * CELL;
        int py = MARGIN + (2 - p.y()) * CELL;

        // Sombra y pieza escaladas al nuevo tamaño de celda
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fillOval(px + CELL / 2 - 26, py + CELL / 2 - 24, 56, 56);

        g2.setColor(new Color(13, 110, 253));
        g2.fillOval(px + CELL / 2 - 28, py + CELL / 2 - 28, 56, 56);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g2.drawString("PIEZA", px + CELL / 2 - 19, py + CELL / 2 + 5);
    }

    private void drawGripper(Graphics2D g2, int x, int y, boolean holding) {
        int gx = MARGIN + x * CELL;
        int gy = MARGIN + (2 - y) * CELL;

        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Color boxColor = holding ? new Color(220, 53, 69) : new Color(25, 135, 84);
        g2.setColor(boxColor);
        g2.drawRoundRect(gx + 15, gy + 15, CELL - 30, CELL - 30, 16, 16);

        if (holding) {
            g2.setColor(new Color(220, 53, 69, 50));
            g2.fillRoundRect(gx + 15, gy + 15, CELL - 30, CELL - 30, 16, 16);
        }

        g2.setColor(new Color(33, 37, 41));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String labelText = holding ? "Garra [Cargada]" : "Garra [Libre]";
        g2.drawString(labelText, gx + 15, gy + CELL - 15);
    }
}