package com.boomaa.opends.usb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DirectInputDevice extends Controller {
    private final List<DIDeviceObject> objects = new ArrayList<>();
    private final int[] deviceState;
    private final long address;
    private final byte[] instanceGUID;
    private final byte[] productGUID;
    private final int devSubtype;
    private final String instanceName;
    private final String productName;
    private Controller.Type devType;
    private int currentFormatOffset;

    public DirectInputDevice(long address, byte[] instanceGUID, byte[] productGUID, int devType, int devSubtype, String instanceName, String productName) {
        super();
        this.address = address;
        this.instanceGUID = instanceGUID;
        this.productGUID = productGUID;
        for (Controller.Type type : Controller.Type.values()) {
            if (type.getDirectInputFlag() == devType && devType != -1) {
                this.devType = type;
                break;
            }
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
        setCooperativeLevel(DIFlags.DISCL_BACKGROUND | DIFlags.DISCL_EXCLUSIVE);
        acquire();
        this.deviceState = new int[objects.size()];
    }

    public void enumObjects() {
        checkReleaseIOException(enumObjects(address, DIFlags.DIDFT_BUTTON | DIFlags.DIDFT_AXIS), "enumerate objects", DIFlags.DI_OK);
    }

    private native int enumObjects(long address, int flags);

    private static native int poll(long address);

    public void acquire() {
        checkReleaseIOException(acquire(address), "acquire", DIFlags.DI_OK, DIFlags.DIERR_OTHERAPPHASPRIO, DIFlags.DI_NOEFFECT);
    }

    private static native int acquire(long address);

    public void unacquire() {
        checkReleaseIOException(unacquire(address), "unacquire", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
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
        checkReleaseIOException(value, "get device state", DIFlags.DI_OK);
    }

    private static native int fetchDeviceState(long address, int[] deviceState);

    public int setDataFormat(int flags) {
        DIDeviceObject[] deviceObjects = new DIDeviceObject[objects.size()];
        objects.toArray(deviceObjects);
        return setDataFormat(address, flags, deviceObjects);
    }

    private static native int setDataFormat(long address, int flags, DIDeviceObject[] deviceObjects);

    public synchronized void setCooperativeLevel(int flags) {
        checkReleaseIOException(setCooperativeLevel(address, PlaceholderWindow.getInstance().getHwnd(), flags), "set cooperative level", DIFlags.DI_OK);
    }

    private static native int setCooperativeLevel(long address, long hwndAddress, int flags);

    public synchronized long[] fetchRange(int objectId) {
        long[] range = new long[2];
        checkReleaseIOException(fetchRangeProperty(address, objectId, range), "get object range", DIFlags.DI_OK, DIFlags.DIERR_UNSUPPORTED);
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

    private static IOException checkThrownIOException(int value, String failureMsg, int... failFlags) {
        boolean valInFlags = false;
        for (int flag : failFlags) {
            if (value == flag) {
                valInFlags = true;
                break;
            }
        }
        return valInFlags ? null : new IOException("Failed to " + failureMsg + " (" + Integer.toHexString(value) + ")");
    }

    private void checkReleaseIOException(int value, String failureMsg, int... failFlags) {
        IOException e = checkThrownIOException(value, failureMsg, failFlags);
        if (e != null) {
            super.remove();
        }
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
        checkReleaseIOException(value, "poll", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
        fetchDeviceState();
    }

    @Override
    public String getName() {
        return productName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectInputDevice that = (DirectInputDevice) o;
        return devSubtype == that.devSubtype &&
                Arrays.equals(instanceGUID, that.instanceGUID) &&
                Arrays.equals(productGUID, that.productGUID) &&
                instanceName.equals(that.instanceName) &&
                productName.equals(that.productName) &&
                devType == that.devType;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(devSubtype, instanceName, productName, devType);
        result = 31 * result + Arrays.hashCode(instanceGUID);
        result = 31 * result + Arrays.hashCode(productGUID);
        return result;
    }
}