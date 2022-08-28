package com.boomaa.opends.display.elements;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

public class HCheckBox extends HideBase<JCheckBox> {
    public HCheckBox(String text) {
        super(() -> new JCheckBox(text));
    }

    public boolean isSelected() {
        return getElement() != null ? getElement().isSelected() : selected;
    }

    public void setSelected(boolean selected) {
        if (getElement() != null) {
            getElement().setEnabled(selected);
        } else {
            this.selected = selected;
        }
    }

    public void addActionListener(ActionListener listener) {
        if (getElement() != null) {
            getElement().addActionListener(listener);
        }
    }

    public void addItemListener(ItemListener listener) {
        if (getElement() != null) {
            getElement().addItemListener(listener);
        }
    }
}
