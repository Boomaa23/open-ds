package com.boomaa.opends.headless.elements;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

public class HCheckBox extends HideComponent<JCheckBox> {
    protected boolean selected;

    public HCheckBox(String text) {
        super(() -> new JCheckBox(text));
    }

    public boolean isSelected() {
        return !isHeadless() ? getElement().isSelected() : selected;
    }

    public void setSelected(boolean selected) {
        if (!isHeadless()) {
            getElement().setSelected(selected);
        } else {
            this.selected = selected;
        }
    }

    public void addActionListener(ActionListener listener) {
        if (!isHeadless()) {
            getElement().addActionListener(listener);
        }
    }

    public void addItemListener(ItemListener listener) {
        if (!isHeadless()) {
            getElement().addItemListener(listener);
        }
    }
}
