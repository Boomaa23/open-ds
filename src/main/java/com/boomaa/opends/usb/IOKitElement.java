package com.boomaa.opends.usb;

public class IOKitElement implements Component {
    private final Identifier componentId;
    private final long cookie;
    private final int min;
    private final int max;
    private final boolean isButton;
    private final boolean isAxis;
    private double value;

    public IOKitElement(long cookie, int type, int min, int max, int usage, int usagePage) {
        this.cookie = cookie;
        this.min = min;
        this.max = max;
        this.isButton = usagePage == IOKitFlags.UP_BUTTON && type == IOKitFlags.ET_BUTTON;
        this.isAxis = usagePage == IOKitFlags.UP_GENERIC_DESKTOP
                && (type == IOKitFlags.ET_AXIS || type == IOKitFlags.ET_MISC);
        boolean axisValid = isAxis && usage >= IOKitFlags.GD_USAGE_AXISMIN
                && usage < Axis.values().length + IOKitFlags.GD_USAGE_AXISMIN;
        boolean buttonValid = isButton && usage < Button.values().length;
        this.componentId = axisValid ? Component.Axis.values()[usage - IOKitFlags.GD_USAGE_AXISMIN] :
                buttonValid ? Component.Button.values()[usage] : Component.NullIdentifier.NONE;
    }

    public long getCookie() {
        return cookie;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public Identifier getIdentitifer() {
        return componentId;
    }

    @Override
    public double provideValue() {
        return 2 * (value - min) / (max - min) - 1;
    }

    @Override
    public boolean isButton() {
        return isButton;
    }

    @Override
    public boolean isAxis() {
        return isAxis;
    }

    @Override
    public String toString() {
        return getIdentitifer().getName();
    }
}
