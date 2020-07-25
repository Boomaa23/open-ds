package com.boomaa.opends.display.elements;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@SuppressWarnings("serial")
public class OverlayField extends JTextField implements FocusListener {
    private final String hint;
    private boolean showingHint;

    public OverlayField(final String hint, final int col) {
        super(hint, col);
        super.setForeground(Color.GRAY);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    public void reset() {
        super.setVisible(false);
        super.setText(hint);
        showingHint = true;
        super.setVisible(true);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    @Override
    public void setText(String t) {
        showingHint = false;
        super.setText(t);
    }
}
