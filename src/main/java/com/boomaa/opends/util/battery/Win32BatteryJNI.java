package com.boomaa.opends.util.battery;

import com.boomaa.opends.usb.USBInterface;

public class Win32BatteryJNI {
    static {
        //Ensure the JNI library (batteryjni-win32.dll) is loaded by the static initializer of USBInterface (win32 only)
        USBInterface.initLibraries(false);
        System.loadLibrary("batteryjni-win32");
    }

    // https://docs.microsoft.com/en-us/windows/win32/api/winbase/ns-winbase-system_power_status
    public static native boolean isAC();
    public static native int getFlag();
    public static native int getPercent();
    public static native int getLifeTime();
    public static native int getFullTime();
}
