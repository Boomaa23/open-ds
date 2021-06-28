package com.boomaa.opends.usb.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectInputDevice implements Controller {
    private final List<DIDeviceObject> objects = new ArrayList<>();
    private final int[] deviceState;
    private final long address;
    private final byte[] instanceGUID;
    private final byte[] productGUID;
    private final Type devType;
    private final int devSubtype;
    private final String instanceName;
    private final String productName;
    private int currentFormatOffset;
    private int numButtons;

    public DirectInputDevice(long address, byte[] instanceGUID, byte[] productGUID, int devType, int devSubtype, String instanceName, String productName) {
        this.address = address;
        this.instanceGUID = instanceGUID;
        this.productGUID = productGUID;
        switch (devType) {
            case DIFlags.DI8DEVTYPE_JOYSTICK:
                this.devType = Type.STICK;
                break;
            case DIFlags.DI8DEVTYPE_GAMEPAD:
                this.devType = Type.GAMEPAD;
                break;
            default:
                this.devType = Type.UNKNOWN;
                break;
        }
        this.devSubtype = devSubtype;
        this.instanceName = instanceName;
        this.productName = productName;
        enumObjects();
        int axisMode = DIFlags.DIDF_ABSAXIS;
        for (DIDeviceObject obj : objects) {
            if (obj.isRelative()) {
                axisMode = DIFlags.DIDF_RELAXIS;
                break;
            }
        }
        setDataFormat(axisMode);
        setCooperativeLevel(DIFlags.DISCL_BACKGROUND | DIFlags.DISCL_NONEXCLUSIVE);
        acquire();
        this.deviceState = new int[objects.size()];
    }

    public void enumObjects() {
        checkIOException(enumObjects(address, DIFlags.DIDFT_BUTTON | DIFlags.DIDFT_AXIS), "enumerate objects", DIFlags.DI_OK);
    }

    private native int enumObjects(long address, int flags);

    private static native int poll(long address);

    public void acquire() {
        checkIOException(acquire(address), "acquire", DIFlags.DI_OK, DIFlags.DIERR_OTHERAPPHASPRIO, DIFlags.DI_NOEFFECT);
    }

    private static native int acquire(long address);

    public void unacquire() {
        checkIOException(unacquire(address), "unacquire", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
    }

    private static native int unacquire(long address);

    public void release() {
        release(address);
    }

    private static native int release(long address);

    public void fetchDeviceState() {
        int value = fetchDeviceState(address, deviceState);
        if (!ensureAcquired(value)) {
            Arrays.fill(deviceState, 0);
        }
        checkIOException(value, "get device state", DIFlags.DI_OK);
    }

    private static native int fetchDeviceState(long address, int[] deviceState);

    public int setDataFormat(int flags) {
        DIDeviceObject[] deviceObjects = new DIDeviceObject[objects.size()];
        objects.toArray(deviceObjects);
        return setDataFormat(address, flags, deviceObjects);
    }

    private static native int setDataFormat(long address, int flags, DIDeviceObject[] deviceObjects);

    public synchronized void setCooperativeLevel(int flags) {
        checkIOException(setCooperativeLevel(address, PlaceholderWindow.getInstance().getHwnd(), flags), "set cooperative level", DIFlags.DI_OK);
    }

    private static native int setCooperativeLevel(long address, long hwndAddress, int flags);

    public synchronized long[] fetchRange(int objectId) {
        long[] range = new long[2];
        checkIOException(fetchRangeProperty(address, objectId, range), "get object range", DIFlags.DI_OK, DIFlags.DIERR_UNSUPPORTED);
        return range;
    }

    private static native int fetchRangeProperty(long address, int objectId, long[] range);

    public synchronized int fetchDeadband(int objectId) {
        return fetchDeadbandProperty(address, objectId);
    }

    private static native int fetchDeadbandProperty(long address, int objectIdentifier);

    private void addObject(byte[] guid, int guidType, int identifier, int type, int instance, int flags, String name) {
        objects.add(new DIDeviceObject(this, guid, guidType, identifier, type, instance, flags, name, currentFormatOffset++));
    }

    public void incrementNumButtons() {
        numButtons++;
    }

    private static void checkIOException(int value, String failureMsg, int... failFlags) {
        for (int flag : failFlags) {
            if (value == flag) {
                return;
            }
        }
        new IOException("Failed to " + failureMsg + " (" + Integer.toHexString(value) + ")").printStackTrace();
    }

    private boolean ensureAcquired(int value) {
        boolean hasAcquired = value != DIFlags.DIERR_NOTACQUIRED;
        if (!hasAcquired) {
            acquire();
        }
        return hasAcquired;
    }

    public int[] getDeviceState() {
        return deviceState;
    }

    @Override
    public Type getType() {
        return devType;
    }

    @Override
    public Component[] getComponents() {
        return objects.toArray(new Component[0]);
    }

    @Override
    public Component getComponent(Component.Identifier id) {
        for (Component comp : objects) {
            if (comp.getIdentitifer() == id) {
                return comp;
            }
        }
        return null;
    }

    @Override
    public void poll() {
        int value = poll(address);
        ensureAcquired(value);
        checkIOException(value, "poll", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
        fetchDeviceState();
    }

    @Override
    public String getName() {
        return productName;
    }

    @Override
    public int getNumButtons() {
        return numButtons;
    }
}