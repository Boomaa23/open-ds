package com.boomaa.opends.util;

public enum OperatingSystem {
    WINDOWS("win", "win32"),
    MACOS("mac", "osx"),
    UNIX("linux", "linux"),
    UNSUPPORTED("", "");

    private final String key;
    private final String common;

    OperatingSystem(String key, String common) {
        this.key = key;
        this.common = common;
    }

    public String getKey() {
        return key;
    }

    public String getCommonName() {
        return common;
    }

    public static OperatingSystem getCurrent() {
        String search = System.getProperty("os.name").toLowerCase();
        for (OperatingSystem os : OperatingSystem.values()) {
            if (search.contains(os.key)) {
                return os;
            }
        }
        return OperatingSystem.UNSUPPORTED;
    }

    public static boolean isWindows() {
        return getCurrent() == WINDOWS;
    }

    public static UnsupportedOperationException unsupportedException() {
        return new UnsupportedOperationException("Operating system not supported. Switch to Windows/Linux/macOS");
    }
}
