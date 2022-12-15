package com.boomaa.opends.display.elements;

import javax.swing.JLabel;

public class HideableLabel extends JLabel {
    private boolean displayed;

    public HideableLabel(boolean displayedByDefault, String text) {
        super(text);
        this.setVisible(displayedByDefault);
        this.displayed = displayedByDefault;
    }

    public HideableLabel(boolean displayedByDefault) {
        this(displayedByDefault, "");
    }

    public void setDisplay(boolean visible) {
        this.setVisible(visible);
        displayed = visible;
    }

    public void toggleDisplay() {
        displayed = !displayed;
        setDisplay(displayed);
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
    public String getText() {
        return isDisplayed() ? super.getText() : "";
    }

    @Override
    public void setText(String text) {
        setDisplay(true);
        super.setText(text);
    }

    public void setText(int text) {
        this.setText(String.valueOf(text));
    }

    public void setText(double text) {
        this.setText(String.valueOf(text));
    }

    public void setText(float text) {
        this.setText(String.valueOf(text));
    }

    public void setText(Object text) {
        this.setText(String.valueOf(text));
    }
}
