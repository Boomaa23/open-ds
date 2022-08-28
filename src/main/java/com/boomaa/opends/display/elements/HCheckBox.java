package com.boomaa.opends.display.elements;

import com.boomaa.opends.util.Parameter;

import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class HCheckBox {
    private final JCheckBox element;
    private boolean selected;
    private boolean enabled;

    public HCheckBox(String text) {
        this.element = Parameter.HEADLESS.isPresent() ? null : new JCheckBox(text);
    }

    public boolean isEnabled() {
        return element != null ? element.isEnabled() : enabled;
    }

    public boolean isSelected() {
        return element != null ? element.isSelected() : selected;
    }

    public void setEnabled(boolean enabled) {
        if (element != null) {
            element.setEnabled(enabled);
        } else {
            this.enabled = enabled;
        }
    }

    public void setSelected(boolean selected) {
        if (element != null) {
            element.setEnabled(selected);
        } else {
            this.selected = selected;
        }
    }

    public void addActionListener(ActionListener listener) {
        if (element != null) {
            element.addActionListener(listener);
        }
    }

    public void addItemListener(ItemListener listener) {
        if (element != null) {
            element.addItemListener(listener);
        }
    }
}
