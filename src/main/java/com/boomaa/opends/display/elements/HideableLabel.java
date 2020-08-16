package com.boomaa.opends.display.elements;

import javax.swing.JLabel;

public class HideableLabel extends JLabel {
    private boolean displayed;

    public HideableLabel(boolean displayedByDefault, String text) {
        super(text);
        this.setVisible(displayedByDefault);
        this.displayed = displayedByDefault;
    }

    public void setDisplay(boolean visible) {
        this.setVisible(visible);
        displayed = visible;
    }

    public void toggleDisplay() {
        setDisplay(!displayed);
    }

    public void forceDisplay() {
        setDisplay(true);
    }

    public void forceHide() {
        setDisplay(false);
    }

    public boolean isDisplayed() {
        return displayed;
    }

    @Override
    public void setText(String text) {
        setDisplay(true);
        super.setText(text);
    }
}
