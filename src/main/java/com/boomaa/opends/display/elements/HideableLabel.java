package com.boomaa.opends.display.elements;

import javax.swing.JLabel;

public class HideableLabel extends JLabel {
    private boolean isDisplayed;

    public HideableLabel(String text, boolean displayedByDefault) {
        super(text);
        this.setVisible(displayedByDefault);
        this.isDisplayed = displayedByDefault;
    }

    public void setDisplay(boolean visible) {
        this.setVisible(visible);
        isDisplayed = visible;
    }

    public void toggleDisplay() {
        setDisplay(!isDisplayed);
    }

    public void forceDisplay() {
        setDisplay(true);
    }

    public void forceHide() {
        setDisplay(false);
    }
}
