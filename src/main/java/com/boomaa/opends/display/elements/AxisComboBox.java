package com.boomaa.opends.display.elements;

import com.boomaa.opends.usb.Component;

import javax.swing.JComboBox;

public class AxisComboBox extends JComboBox<Component.Identifier> {
    private final String name;
    private final Component.Axis id;

    public AxisComboBox(Component.Axis id) {
        this.name = id.name();
        this.id = id;
    }

    public Component.Axis getUserId() {
        return id;
    }

    public Component.Identifier getHardwareId() {
        return (Component.Identifier) super.getSelectedItem();
    }

    @Override
    public String getName() {
        return name;
    }
}
