package com.boomaa.opends.usb;

import com.boomaa.opends.util.Debug;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class DirectInputDevice extends Controller<DIDeviceObject> {
    private final int[] deviceState;
    private final long address;
    private final int devSubtype;
    private final String instanceName;
    private final String productName;
    private Controller.Type devType;
    private int currentFormatOffset;

    public DirectInputDevice(long address, int devType, int devSubtype, String instanceName, String productName) {
        this.address = address;
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
        setCooperativeLevel(DIFlags.DISCL_BACKGROUND | DIFlags.DISCL_NONEXCLUSIVE);
        acquire();
        this.deviceState = new int[objects.size()];
    }

    public void enumObjects() {
        int retcode = enumObjects(address, DIFlags.DIDFT_BUTTON | DIFlags.DIDFT_AXIS);
        checkRetcodeElseRelease(retcode, "enumerate objects", DIFlags.DI_OK);
    }

    private native int enumObjects(long address, int flags);

    private static native int poll(long address);

    public void acquire() {
        checkRetcodeElseRelease(acquire(address), "acquire", DIFlags.DI_OK, DIFlags.DIERR_OTHERAPPHASPRIO, DIFlags.DI_NOEFFECT);
    }

    private static native int acquire(long address);

    public void unacquire() {
        checkRetcodeElseRelease(unacquire(address), "unacquire", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
    }

    private static native int unacquire(long address);

    public void release() {
        release(address);
    }

    private static native int release(long address);

    public void fetchDeviceState() {
        int retcode = fetchDeviceState(address, deviceState);
        if (!ensureAcquired(retcode)) {
            Arrays.fill(deviceState, 0);
        }
        checkRetcodeElseRelease(retcode, "get device state", DIFlags.DI_OK);
    }

    private static native int fetchDeviceState(long address, int[] deviceState);

    public int setDataFormat(int flags) {
        DIDeviceObject[] deviceObjects = new DIDeviceObject[objects.size()];
        objects.toArray(deviceObjects);
        return setDataFormat(address, flags, deviceObjects);
    }

    private static native int setDataFormat(long address, int flags, DIDeviceObject[] deviceObjects);

    public synchronized void setCooperativeLevel(int flags) {
        int retcode = setCooperativeLevel(address, PlaceholderWindow.getInstance().getHwnd(), flags);
        checkRetcodeElseRelease(retcode, "set cooperative level", DIFlags.DI_OK);
    }

    private static native int setCooperativeLevel(long address, long hwndAddress, int flags);

    public synchronized long[] fetchRange(int dwType) {
        long[] range = new long[2];
        int retcode = fetchRangeProperty(address, dwType, range);
        checkRetcodeElseRelease(retcode, "get object range", DIFlags.DI_OK, DIFlags.DIERR_UNSUPPORTED);
        return range;
    }

    private static native int fetchRangeProperty(long address, int dwType, long[] range);

    private void addObject(byte[] guid, int dwType, int didftType, int didftInstance, int axisIdIdx, String name) {
        objects.add(new DIDeviceObject(this, guid, dwType, didftType, didftInstance, axisIdIdx, name, currentFormatOffset++));
    }

    private static IOException checkRetcode(int retcode, String failureMsg, int... successCodes) {
        for (int code : successCodes) {
            if (retcode == code) {
                return null;
            }
        }
        return new IOException("Failed to " + failureMsg + " (0x" + Integer.toHexString(retcode) + ")");
    }

    private void checkRetcodeElseRelease(int retcode, String failureMsg, int... successCodes) {
        IOException e = checkRetcode(retcode, failureMsg, successCodes);
        if (e != null) {
            Debug.println(e.getMessage());
            super.remove();
        }
    }

    private boolean ensureAcquired(int value) {
        boolean hasAcquired = (value != DIFlags.DIERR_NOTACQUIRED) && (value != DIFlags.DI_NOEFFECT);
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
    public void poll() {
        int retcode = poll(address);
        ensureAcquired(retcode);
        checkRetcodeElseRelease(retcode, "poll", DIFlags.DI_OK, DIFlags.DI_NOEFFECT);
        fetchDeviceState();
    }

    @Override
    public String getName() {
        return productName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DirectInputDevice that = (DirectInputDevice) o;
        return devSubtype == that.devSubtype
                && instanceName.equals(that.instanceName)
                && productName.equals(that.productName)
                && devType == that.devType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(devSubtype, instanceName, productName, devType);
    }
}