package com.boomaa.opends.usb;

public class XboxController extends HIDDevice {
    public XboxController(Controller ctrl) {
        super(ctrl);
    }

    @Override
    public Component.Identifier[] provideAxes() {
        return new Component.Identifier[] {
                Component.Axis.X,
                Component.Axis.Y,
                Component.NullIdentifier.NONE, //left trigger
                Component.NullIdentifier.NONE, //right trigger
                Component.Axis.RX,
                Component.Axis.RY,
        };
    }
}
