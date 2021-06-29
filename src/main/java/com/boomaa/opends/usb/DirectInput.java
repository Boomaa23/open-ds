package com.boomaa.opends.usb;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DirectInput {
    public static final DirectInput INSTANCE = new DirectInput();
    private final List<DirectInputDevice> devices = new LinkedList<>();
    private final long directInputAddress;

    private DirectInput() {
        this.directInputAddress = create();
        enumDevices();
    }

    private native long create();

    public void enumDevices() {
        try {
            enumDevices(directInputAddress);
        } catch (IOException e) {
            release();
        }
    }

    private native void enumDevices(long address) throws IOException;

    private void addDevice(long address, byte[] instanceGUID, byte[] productGUID, int devType, int devSubtype, String instanceName, String productName) {
        DirectInputDevice device = new DirectInputDevice(address, instanceGUID, productGUID, devType, devSubtype, instanceName, productName);
        if (!devices.contains(device)) {
            devices.add(device);
        }
    }

    public void clear() {
        devices.clear();
    }

    public void release() {
        for (DirectInputDevice device : devices) {
            device.release();
        }
        release(directInputAddress);
    }

    private native void release(long address);

    public List<DirectInputDevice> getDevices() {
        return devices;
    }
}