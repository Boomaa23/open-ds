package com.boomaa.opends.display;

import javax.swing.JButton;

public class StickyButton extends JButton {
    private boolean wasPressed = false;

    public StickyButton(String title) {
        super(title);
    }

    public boolean wasPressed() {
        boolean last = wasPressed;
        wasPressed = false;
        return last;
    }
}
