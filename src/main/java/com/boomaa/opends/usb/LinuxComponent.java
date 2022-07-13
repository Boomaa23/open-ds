package com.boomaa.opends.usb;

public class LinuxComponent implements Component {
    private final Identifier componentId;
    private final byte type;
    private final int number;
    private double value;

    public LinuxComponent(LinuxJSEvent event) {
        this.type = event.getType();
        this.number = event.getNumber();
        this.value = event.getValue();
        Identifier tempId = isButton()
            ? Component.Button.values()[number]
            : Component.Axis.values()[number];
        //TODO make a more permanent solution, DirectInput auto-assigns RZ
        if (tempId == Axis.Z) {
            tempId = Axis.RZ;
        } else if (tempId == Axis.RZ) {
            tempId = Axis.Z;
        }
        this.componentId = tempId;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public byte getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public Identifier getIdentitifer() {
        return componentId;
    }

    @Override
    public double provideValue() {
        return value;
    }

    @Override
    public boolean isButton() {
        return (type & LinuxFlags.JS_EVENT_BUTTON) != 0;
    }

    @Override
    public boolean isAxis() {
        return (type & LinuxFlags.JS_EVENT_AXIS) != 0;
    }
}
