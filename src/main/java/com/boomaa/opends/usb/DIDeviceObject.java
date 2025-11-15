package com.boomaa.opends.usb;

public class DIDeviceObject implements Component {
    private final DirectInputDevice device;
    private final byte[] guid;
    private final int didftInstance;
    private final int didftType;
    private final String name;
    private final int formatOffset;
    private final long min;
    private final long max;
    private final boolean isButton;
    private final boolean isAxis;
    private final boolean isRelative;
    private Component.Identifier componentId;

    public DIDeviceObject(DirectInputDevice device, byte[] guid, int dwType,
                          int didftInstance, int didftType, int axisIdIdx, String name, int formatOffset) {
        this.device = device;
        this.guid = guid;
        this.didftInstance = didftInstance;
        this.didftType = didftType;
        this.name = name;
        this.formatOffset = formatOffset;

        this.isButton = (didftType & DIFlags.DIDFT_BUTTON) != 0;
        this.isAxis = (didftType & DIFlags.DIDFT_AXIS) != 0;
        this.isRelative = isAxis && ((didftType & DIFlags.DIDFT_RELAXIS) != 0);

        if (isButton) {
            int newNumButtons = device.incrementNumButtons();
            componentId = Component.Button.values()[newNumButtons];
        } else if (isAxis) {
            device.incrementNumAxes();
            componentId = Component.Axis.values()[axisIdIdx];
        }
        if (componentId == null) {
            componentId = Component.Axis.UNKNOWN;
        }
        long[] range = device.fetchRange(dwType);
        this.min = range[0];
        this.max = range[1];
    }

    @Override
    public boolean isButton() {
        return isButton;
    }

    @Override
    public boolean isAxis() {
        return isAxis;
    }

    public final boolean isRelative() {
        return isRelative;
    }

    public byte[] getGUID() {
        return guid;
    }

    public int getDIDFTInstance() {
        return didftInstance;
    }

    public int getDIDFTType() {
        return didftType;
    }

    @Override
    public Component.Identifier getIdentitifer() {
        return componentId;
    }

    @Override
    public double provideValue() {
        int value = device.getDeviceState()[formatOffset];
        if (isButton) {
            return (value & 0x80) >> 7;
        } else if (isAxis) {
            return 2 * (value - min) / (float) (max - min) - 1;
        }
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
