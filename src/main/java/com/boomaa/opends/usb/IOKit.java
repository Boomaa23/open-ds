package com.boomaa.opends.usb;

public class IOKit extends NativeUSBManager<IOKitDevice> {
    private long address;

    public IOKit() {
        enumDevices();
    }

    private native long createIterator();

    @Override
    public void enumDevices() {
        this.address = createIterator();
        IOKitDevice dev;
        while ((dev = next(address)) != null) {
            long usage = dev.getUsage();
            if (dev.getUsagePage() != IOKitFlags.UP_GENERIC_DESKTOP
                || (usage != IOKitFlags.USAGE_JOYSTICK && usage != IOKitFlags.USAGE_GAMEPAD)) {
                continue;
            }
            boolean inDevices = false;
            for (IOKitDevice loopDev : devices) {
                if (loopDev.getAddress() == dev.getAddress()) {
                    inDevices = true;
                    break;
                }
            }
            if (!inDevices) {
                devices.add(dev);
            }
        }
    }

    private native IOKitDevice next(long address);

    public void close() {
        close(address);
    }

    private native int close(long address);
}
