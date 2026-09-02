package com.robot.arm.infrastructure.gui;

import com.robot.arm.domain.model.GripperState;
import com.robot.arm.domain.model.RobotState;

import javax.swing.*;
import java.awt.*;

public class GridPanel extends JPanel {
    private RobotState currentState;

    public GridPanel() {
        this.currentState = RobotState.initial();
        setPreferredSize(new Dimension(460, 460));
        setBackground(new Color(245, 247, 250));
    }

    public void updateState(RobotState state) {
        this.currentState = state;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int cellSize = (Math.min(width, height) - 2 * padding) / 3;

        int startX = (width - cellSize * 3) / 2;
        int startY = (height - cellSize * 3) / 2;

        // Dibujar Cuadrícula 3x3
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int cellX = startX + x * cellSize;
                int cellY = startY + (2 - y) * cellSize; // Conversión a coordenadas cartesianas

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(cellX + 4, cellY + 4, cellSize - 8, cellSize - 8, 12, 12);
                g2.setColor(new Color(210, 215, 225));
                g2.drawRoundRect(cellX + 4, cellY + 4, cellSize - 8, cellSize - 8, 12, 12);

                g2.setColor(new Color(140, 150, 165));
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString("(" + x + ", " + y + ")", cellX + 10, cellY + 20);
            }
        }

        // Dibujar Pieza 1 (si está en el tablero)
        if (currentState != null && currentState.piece1() != null && !currentState.piece1().isHeld()) {
            int px = currentState.piece1().gridPosition().x();
            int py = currentState.piece1().gridPosition().y();
            drawPiece(g2, startX, startY, cellSize, px, py, "P1", new Color(41, 128, 185));
        }

        // Dibujar Pieza 2 (si está en el tablero)
        if (currentState != null && currentState.piece2() != null && !currentState.piece2().isHeld()) {
            int px = currentState.piece2().gridPosition().x();
            int py = currentState.piece2().gridPosition().y();
            drawPiece(g2, startX, startY, cellSize, px, py, "P2", new Color(211, 84, 0));
        }

        // Dibujar Garra Robótica en la posición actual
        if (currentState != null) {
            int armX = currentState.x();
            int armY = currentState.y();
            drawRobotArm(g2, startX, startY, cellSize, armX, armY, currentState.gripperState());
        }
    }

    private void drawPiece(Graphics2D g2, int startX, int startY, int cellSize, int x, int y, String label, Color color) {
        int cellX = startX + x * cellSize;
        int cellY = startY + (2 - y) * cellSize;
        int size = cellSize / 3;
        int cx = cellX + (cellSize - size) / 2;
        int cy = cellY + (cellSize - size) / 2;

        g2.setColor(color);
        g2.fillOval(cx, cy, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx + (size - fm.stringWidth(label)) / 2;
        int ty = cy + (size + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(label, tx, ty);
    }

    private void drawRobotArm(Graphics2D g2, int startX, int startY, int cellSize, int x, int y, GripperState gripper) {
        int cellX = startX + x * cellSize;
        int cellY = startY + (2 - y) * cellSize;

        // Marco verde resaltado para la celda actual
        g2.setColor(new Color(46, 204, 113, 50));
        g2.fillRoundRect(cellX + 6, cellY + 6, cellSize - 12, cellSize - 12, 10, 10);
        g2.setColor(new Color(39, 174, 96));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cellX + 6, cellY + 6, cellSize - 12, cellSize - 12, 10, 10);

        int cx = cellX + cellSize / 2;
        int cy = cellY + cellSize / 2;

        // Estructura visual de la pinza
        g2.setColor(new Color(44, 62, 80));
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(cx - 20, cy - 22, cx + 20, cy - 22);
        g2.drawLine(cx - 15, cy - 22, cx - 15, cy - 8);
        g2.drawLine(cx + 15, cy - 22, cx + 15, cy - 8);

        // Badge si está sosteniendo una pieza
        if (gripper == GripperState.HOLDING_PIECE_1) {
            drawHeldBadge(g2, cx, cy, "PINZA: [P1]", new Color(41, 128, 185));
        } else if (gripper == GripperState.HOLDING_PIECE_2) {
            drawHeldBadge(g2, cx, cy, "PINZA: [P2]", new Color(211, 84, 0));
        }
    }

    private void drawHeldBadge(Graphics2D g2, int cx, int cy, String text, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(cx - 38, cy - 5, 76, 20, 8, 8);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, cx - fm.stringWidth(text) / 2, cy + 9);
    }
}