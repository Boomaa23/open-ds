package com.boomaa.opends.display.elements;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class RSLIndicator extends JComponent {
    private static final int FLASH_DELAY_MS = 250;
    private static final Color START_COLOR = Color.LIGHT_GRAY;
    private static final Color FLASH_COLOR = new Color(255, 163, 31);

    private final Timer flashTimer;
    private boolean drawFlashColor = true;

    public RSLIndicator() {
        this.flashTimer = new Timer(FLASH_DELAY_MS, e -> draw(getGraphics(), toggleColor()));
        flashTimer.setInitialDelay(0);
    }

    private Color toggleColor() {
        drawFlashColor = !drawFlashColor;
        return !drawFlashColor ? FLASH_COLOR : START_COLOR;
    }

    private void draw(Graphics g, Color c) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        g2.setColor(c);
        g2.fillOval(14, 14, 14, 14);

        g2.drawLine(21, 10, 21, 0);
        g2.drawLine(21, 32, 21, 41);
        g2.drawLine(10, 21, 0, 21);
        g2.drawLine(31, 21, 41, 21);

        g2.drawLine(5, 5, 10, 10);
        g2.drawLine(31, 31, 36, 36);
        g2.drawLine(36, 5, 31, 10);
        g2.drawLine(5, 36, 10, 31);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g, START_COLOR);
    }

    public void setFlashing(boolean isFlashing) {
        if (isFlashing) {
            flashTimer.start();
        } else {
            flashTimer.stop();
            drawFlashColor = true;
            paintComponent(getGraphics());
        }
    }
}
