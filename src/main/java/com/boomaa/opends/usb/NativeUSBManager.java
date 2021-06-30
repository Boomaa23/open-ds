package com.boomaa.opends.usb;

import com.boomaa.opends.util.OperatingSystem;

import java.util.LinkedList;
import java.util.List;

public abstract class NativeUSBManager<T extends Controller> {
    private static NativeUSBManager<?> platformManager;
    protected final List<T> devices = new LinkedList<>();

    public abstract void enumDevices();

    public List<T> getDevices() {
        return devices;
    }

    public void clearDevices() {
        devices.clear();
    }

    public static NativeUSBManager<?> getOSInstance() {
        if (platformManager == null) {
            switch (OperatingSystem.getCurrent()) {
                case WINDOWS:
                    platformManager = new DirectInput();
                    break;
                case UNIX:
                    platformManager = new LinuxJoystickAPI();
                    break;
                case MACOS:
                default:
                    throw OperatingSystem.unsupportedException();
            }
        }
        return platformManager;
    }
}
