package com.robot.arm.infrastructure.gui;

import com.robot.arm.domain.model.AutomatonStateNode;
import com.robot.arm.domain.model.RobotState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;

public class GraphPanel extends JPanel {
    private RobotState currentState;

    public GraphPanel() {
        this.currentState = RobotState.initial();
        setPreferredSize(new Dimension(450, 460));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createTitledBorder("Grafo de Transición Dinámico"));
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

        AutomatonStateNode activeNode = (currentState != null) ? currentState.activeNode() : AutomatonStateNode.Q0_INITIAL;

        int w = getWidth();
        int h = getHeight();

        Point p0 = new Point(w / 2, h - 50);
        Point p1 = new Point(w / 2, h / 2 + 10);
        Point p2 = new Point(w - 70, 70);
        Point p3 = new Point(70, 70);

        // Aristas simples (Offset = 0)
        drawEdge(g2, p0, p1, "U,D,L,R", 0);
        drawEdge(g2, p0, p2, "+ P1/P2", 0);
        drawEdge(g2, p0, p3, "- !P1!P2", 0);

        // Aristas curvas bidireccionales (Offset de 35 para separarlas)
        drawEdge(g2, p1, p2, "+ P1/P2", 35);
        drawEdge(g2, p2, p1, "U,D,L,R", 35);

        drawEdge(g2, p1, p3, "- !P1!P2 / + !P1!P2", 35);
        drawEdge(g2, p3, p1, "U,D,L,R", 35);

        drawEdge(g2, p2, p3, "- libre", 35);
        drawEdge(g2, p3, p2, "+ P1/P2", 35);

        // Dibujar nodos
        drawNode(g2, p0, "q0", AutomatonStateNode.Q0_INITIAL == activeNode);
        drawNode(g2, p1, "q1", AutomatonStateNode.Q1_MOVING == activeNode);
        drawNode(g2, p2, "q2", AutomatonStateNode.Q2_HOLDING == activeNode);
        drawNode(g2, p3, "q3", AutomatonStateNode.Q3_RELEASED == activeNode);
    }

    private void drawEdge(Graphics2D g2, Point p1, Point p2, String text, int curveOffset) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        int radius = 32; // Evitar que la flecha quede debajo del nodo
        double startX = p1.x + (dx / dist) * radius;
        double startY = p1.y + (dy / dist) * radius;
        double endX = p2.x - (dx / dist) * radius;
        double endY = p2.y - (dy / dist) * radius;

        double mx = (startX + endX) / 2.0;
        double my = (startY + endY) / 2.0;

        // Vector normal para curvar la línea
        double nx = -dy / dist;
        double ny = dx / dist;

        double cx = mx + nx * curveOffset;
        double cy = my + ny * curveOffset;

        QuadCurve2D curve = new QuadCurve2D.Double(startX, startY, cx, cy, endX, endY);

        g2.setColor(new Color(150, 160, 175));
        g2.setStroke(new BasicStroke(1.8f));
        g2.draw(curve);

        // Flecha direccional
        double dirX = endX - cx;
        double dirY = endY - cy;
        double dirDist = Math.sqrt(dirX * dirX + dirY * dirY);
        double ux = dirX / dirDist;
        double uy = dirY / dirDist;

        int arrowSize = 10;
        int[] xPoints = {(int) endX, (int) (endX - arrowSize * ux + arrowSize * 0.5 * uy), (int) (endX - arrowSize * ux - arrowSize * 0.5 * uy)};
        int[] yPoints = {(int) endY, (int) (endY - arrowSize * uy - arrowSize * 0.5 * ux), (int) (endY - arrowSize * uy + arrowSize * 0.5 * ux)};
        g2.fillPolygon(xPoints, yPoints, 3);

        // Etiqueta legible con fondo
        if (text != null && !text.isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();
            int tx = (int) cx - tw / 2;
            int ty = (int) cy + th / 2 - 2;

            g2.setColor(new Color(245, 247, 250, 230)); // Fondo casi opaco
            g2.fillRoundRect(tx - 3, ty - th - 1, tw + 6, th + 4, 8, 8);

            g2.setColor(new Color(44, 62, 80));
            g2.drawString(text, tx, ty);
        }
    }

    private void drawNode(Graphics2D g2, Point p, String label, boolean isActive) {
        int radius = 28;
        if (isActive) {
            g2.setColor(new Color(46, 204, 113, 80));
            g2.fillOval(p.x - radius - 8, p.y - radius - 8, (radius + 8) * 2, (radius + 8) * 2);
            g2.setColor(new Color(39, 174, 96));
        } else {
            g2.setColor(new Color(52, 152, 219));
        }
        g2.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, p.x - fm.stringWidth(label) / 2, p.y + fm.getAscent() / 2 - 2);
    }
}