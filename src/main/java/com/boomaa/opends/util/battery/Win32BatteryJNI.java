package com.boomaa.opends.util.battery;

import com.boomaa.opends.util.Libraries;

public class Win32BatteryJNI {
    static {
        //Ensure the JNI library (batteryjni-win32.dll) is loaded by the library initializer (win32 only)
        Libraries.init(false);
        System.loadLibrary("batteryjni-win32");
    }

    // https://docs.microsoft.com/en-us/windows/win32/api/winbase/ns-winbase-system_power_status
    public static native boolean isAC();
    public static native int getFlag();
    public static native int getPercent();
    public static native int getLifeTime();
    public static native int getFullTime();
}
