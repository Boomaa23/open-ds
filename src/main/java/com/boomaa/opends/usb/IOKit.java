package com.boomaa.opends.usb;

public class IOKit extends NativeUSBManager<IOKitDevice> {
    private final long address;

    public IOKit() {
        this.address = createIterator();
        enumDevices();
    }

    private native long createIterator();

    @Override
    public void enumDevices() {
        IOKitDevice dev;
        while ((dev = next(address)) != null) {
            devices.add(dev);
        }
    }

    private native IOKitDevice next(long address);

    public void close() {
        close(address);
    }

    private native int close(long address);
}
