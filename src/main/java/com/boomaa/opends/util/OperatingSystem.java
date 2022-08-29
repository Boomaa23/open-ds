package com.boomaa.opends.util;

public enum OperatingSystem {
    WINDOWS("win", "win32", "dll"),
    MACOS("mac", "osx", "jnilib"),
    UNIX("linux", "linux", "so");

    private final String key;
    private final String common;
    private final String nativeExt;

    OperatingSystem(String key, String common, String nativeExt) {
        this.key = key;
        this.common = common;
        this.nativeExt = nativeExt;
    }

    public String getKey() {
        return key;
    }

    public String getCommonName() {
        return common;
    }

    public String getNativeExt() {
        return nativeExt;
    }

    public static OperatingSystem getCurrent() {
        String search = System.getProperty("os.name").toLowerCase();
        for (OperatingSystem os : OperatingSystem.values()) {
            if (search.contains(os.key)) {
                return os;
            }
        }
        throw new NativeSystemError("Unsupported operating system " + search);
    }

    public static String getTempFolder() {
        String tmpPath = System.getProperty("java.io.tmpdir");
        String fSep = System.getProperty("file.separator");
        if (!tmpPath.endsWith(fSep)) {
            tmpPath += fSep;
        }
        return tmpPath;
    }

    public static boolean isWindows() {
        return getCurrent() == WINDOWS;
    }
}
