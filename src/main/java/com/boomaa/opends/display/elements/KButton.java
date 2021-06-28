package com.boomaa.opends.display.elements;

import javax.swing.JButton;

// The joke is that K comes after J so it's better
// Disables enter key on buttons so it works for disabled
public class KButton extends JButton {
    public KButton(String title) {
        super(title);
        this.setFocusable(false);
    }
}
