package com.boomaa.opends.usb.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DirectInput {
    private final List<DirectInputDevice> devices = new ArrayList<>();
    private final long directInputAddress;

    public DirectInput() {
        this.directInputAddress = create();
        try {
            enumDevices(directInputAddress);
        } catch (IOException e) {
            release();
        }
    }

    private native long create();

    private native void enumDevices(long address) throws IOException;

    private void addDevice(long address, byte[] instanceGUID, byte[] productGUID, int devType, int devSubtype, String instanceName, String productName) {
        devices.add(new DirectInputDevice(address, instanceGUID, productGUID, devType, devSubtype, instanceName, productName));
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