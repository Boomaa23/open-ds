package com.boomaa.opends.display.elements;

import javax.swing.JButton;

public class StickyButton extends JButton {
    private final int stickyDuration;
    private int useCount = 0;
    private boolean wasPressed = false;

    public StickyButton(String title, int stickyDuration) {
        super(title);
        this.stickyDuration = stickyDuration;
        this.addActionListener(e -> wasPressed = true);
    }

    public boolean wasPressed() {
        if (wasPressed) {
            if (++useCount > stickyDuration) {
                wasPressed = false;
                useCount = 0;
            } else {
                return true;
            }
        }
        return false;
    }
}
