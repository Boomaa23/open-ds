package com.boomaa.opends.usb;

/**
 * A virtual USB component whose value can be set programmatically,
 * used by VirtualController to simulate gamepad/joystick inputs
 * from keyboard or on-screen buttons.
 */
public class VirtualComponent implements Component {
    private final Identifier identifier;
    private final boolean isAxis;
    private double value;

    public VirtualComponent(Identifier identifier, boolean isAxis) {
        this.identifier = identifier;
        this.isAxis = isAxis;
        this.value = 0;
    }

    @Override
    public Identifier getIdentitifer() {
        return identifier;
    }

    @Override
    public double provideValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean isButton() {
        return !isAxis;
    }

    @Override
    public boolean isAxis() {
        return isAxis;
    }
}
