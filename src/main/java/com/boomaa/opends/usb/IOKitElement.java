package com.boomaa.opends.usb;

public class IOKitElement implements Component {
    private final Identifier componentId;
    private final long cookie;
    private final int type;
    private final int min;
    private final int max;
    private final int usage;
    private final int usagePage;
    private double value;

    public IOKitElement(long cookie, int type, int min, int max, int usage, int usagePage) {
        this.cookie = cookie;
        this.type = type;
        this.min = min;
        this.max = max;
        this.usage = usage;
        this.usagePage = usagePage;
        this.componentId = isButton() ? Component.Button.values()[usage] :
                Component.Axis.values()[usage - IOKitFlags.GD_USAGE_AXISMIN];
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
    public double getValue() {
        return 2 * (value - min) / (max - min) - 1;
    }

    @Override
    public boolean isButton() {
        return usagePage == IOKitFlags.UP_BUTTON && type == IOKitFlags.ET_BUTTON;
    }

    @Override
    public boolean isAxis() {
        return usagePage == IOKitFlags.UP_GENERIC_DESKTOP && type == IOKitFlags.ET_AXIS;
    }
}
