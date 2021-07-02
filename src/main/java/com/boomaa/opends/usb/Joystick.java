package com.boomaa.opends.usb;

public class Joystick extends HIDDevice {
    public Joystick(Controller<?> ctrl) {
        super(ctrl);
    }

    @Override
    public Component.Identifier[] provideAxes() {
        return new Component.Identifier[] {
                Component.Axis.X,
                Component.Axis.Y,
                Component.Axis.RZ
        };
    }
}
