package com.boomaa.opends.usb;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

public class USBUtils {
    public static void listUSBDevices() {
        HidServices hidServices = HidManager.getHidServices();
        for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
            if (hidDevice.getManufacturer().contains("Logitech")) {
                //TODO make this read joystick data
                System.out.println(hidDevice);
            }
        }

    }
}