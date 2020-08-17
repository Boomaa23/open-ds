package com.boomaa.opends.util.battery;

import com.boomaa.opends.util.OperatingSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BatteryInfo {
    private static OperatingSystem os = OperatingSystem.getCurrent();
    private static boolean hasBattery = hasBattery();

    static {
        // Ensure the user is using a supported operating system for JNI (win32) or UNIX command line calls
        if (os != OperatingSystem.WINDOWS && os != OperatingSystem.UNIX) {
            throw new UnsupportedOperationException("Operating system not supported. Switch to Windows/UNIX");
        }
    }

    //TODO make sure this works (windows JNI and linux access)

    // Check if a battery exists
    public static boolean hasBattery() {
        return (os == OperatingSystem.WINDOWS) ? !(Win32BatteryJNI.getFlag() == BatteryFill.NONE.getValue()) : checkLinuxBatteryExists();
    }

    // Checks if the battery is plugged in
    public static boolean isPluggedIn() {
        return hasBattery && ((os == OperatingSystem.WINDOWS) ? Win32BatteryJNI.isAC() : !getLinuxBatteryCall("status").equals("Discharging"));
    }

    // Get rough battery percentage estimate
    public static BatteryFill getFlag() {
        if (hasBattery) {
            if (os == OperatingSystem.WINDOWS) {
                int winapival = Win32BatteryJNI.getFlag();
                BatteryFill baseFill = BatteryFill.UNKNOWN;
                for (BatteryFill flag : BatteryFill.values()) {
                    if (flag.getValue() == winapival) {
                        baseFill = flag;
                        break;
                    }
                }
                int percent = getPercent();
                if (percent == 100) {
                    return BatteryFill.FULL;
                } else if (percent > 33 && percent < 66 && baseFill == BatteryFill.UNKNOWN) {
                    return BatteryFill.NORMAL;
                }
                return baseFill;
            } else if (os == OperatingSystem.UNIX) {
                String capacity = getLinuxBatteryCall("capacity_level");
                for (BatteryFill flag : BatteryFill.values()) {
                    if (flag.name().equals(capacity.toUpperCase())) {
                        return flag;
                    }
                }
            }
        }
        return BatteryFill.UNKNOWN;
    }

    // Current charge of battery (in percent)
    public static int getPercent() {
        if (hasBattery) {
            if (os == OperatingSystem.WINDOWS) {
                return Win32BatteryJNI.getPercent();
            } else if (os == OperatingSystem.UNIX) {
                return Integer.parseInt(getLinuxBatteryCall("capacity"));
            }
        }
        return -1;
    }

    // Time (in minutes) to deplete the battery from the current charge
    public static double getLifeTime() {
        if (hasBattery) {
            if (os == OperatingSystem.WINDOWS) {
                int raw = Win32BatteryJNI.getLifeTime();
                return raw != -1 ? secondsToMinutes(raw) : -1;
            } else if (os == OperatingSystem.UNIX) {
                double chargeNow = Integer.parseInt(getLinuxBatteryCall("charge_now"));
                double currentNow = Integer.parseInt(getLinuxBatteryCall("current_now"));
                return currentNow != 0 ? hoursToMinutes(chargeNow / currentNow) : Double.POSITIVE_INFINITY;
            }
        }
        return -1;
    }

    // Time (in minutes) to deplete a full battery
    public static double getFullTime() {
        if (hasBattery) {
            if (os == OperatingSystem.WINDOWS) {
                int raw = Win32BatteryJNI.getFullTime();
                return raw != -1 ? secondsToMinutes(raw) : -1;
            } else if (os == OperatingSystem.UNIX) {
                double chargeFull = Integer.parseInt(getLinuxBatteryCall("charge_full"));
                double currentNow = Integer.parseInt(getLinuxBatteryCall("current_now"));
                return currentNow != 0 ? hoursToMinutes(chargeFull / currentNow) : Double.POSITIVE_INFINITY;
            }
        }
        return -1;
    }

    private static String getLinuxBatteryCall(String access) {
        try {
            Process p = Runtime.getRuntime().exec("cat /sys/class/power_supply/BAT0/" + access);
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return r.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Given access \"" + access + "\" did not return a valid string");
    }

    private static boolean checkLinuxBatteryExists() {
        try {
            Process p = Runtime.getRuntime().exec("if test -d /sys/class/power_supply/BAT0; then echo \"true\"; else echo \"false\"; fi");
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return Boolean.parseBoolean(r.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static double toWattHour(int in) {
        return in / 1_000_000.0;
    }

    private static double secondsToMinutes(int in) {
        return in / 60.0;
    }

    private static double hoursToMinutes(double in) {
        return in * 60.0;
    }
}
