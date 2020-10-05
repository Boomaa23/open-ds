package com.boomaa.opends.usb.input;

public class DIDeviceObject implements Component {
    private final DirectInputDevice device;
    private final byte[] guid;
    private final int guidType;
    private final int objectId;
    private final int type;
    private final int instance;
    private final int flags;
    private final String name;
    private final int formatOffset;
    private final int deadband;
    private final long min;
    private final long max;
    private Component.Identifier componentId;

    public DIDeviceObject(DirectInputDevice device, byte[] guid, int guidType, int objectId, int type, int instance, int flags, String name, int formatOffset) {
        this.device = device;
        this.guid = guid;
        this.guidType = guidType;
        this.objectId = objectId;
        this.type = type;
        this.instance = instance;
        this.flags = flags;
        this.name = name;
        this.formatOffset = formatOffset;
        if (guidType == DIFlags.BUTTON_GUID) {
            device.incrementNumButtons();
            for (Component.Button searchGuid : Component.Button.values()) {
                if (searchGuid.ordinal() == guidType) { //TODO Can't check buttons like this
                    componentId = searchGuid;
                    break;
                }
            }
        } else {
            for (Component.Axis searchGuid : Component.Axis.values()) {
                if (searchGuid.ordinal() == guidType) {
                    componentId = searchGuid;
                    break;
                }
            }
        }
        if (componentId == null) {
            componentId = Component.Axis.UNKNOWN;
        }
        this.deadband = device.fetchDeadband(objectId);
        long[] range = device.fetchRange(objectId);
        this.min = range[0];
        this.max = range[1];
    }

    public boolean isButton() {
        return (type & DIFlags.DIDFT_BUTTON) != 0;
    }

    public boolean isAxis() {
        return (type & DIFlags.DIDFT_AXIS) != 0;
    }

    public byte[] getGUID() {
        return guid;
    }

    public int getType() {
        return type;
    }

    public int getInstance() {
        return instance;
    }

    @Override
    public Component.Identifier getIdentitifer() {
        return componentId;
    }

    @Override
    public double getDeadband() {
        return deadband;
    }

    @Override
    public double getValue() {
        int value = device.getDeviceState()[formatOffset];
        if (isButton()) {
            return (value & 0x80) != 0 ? 1 : 0;
        } else if (isAxis()) {
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
