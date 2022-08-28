package com.boomaa.opends.util;

import com.boomaa.opends.display.StdRedirect;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Map;

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

    public static void setEnv(String key, String value) {
        try {
            StdRedirect.ERR.toNull();
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);
            StdRedirect.ERR.reset();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }

    public static void disableEnvWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // ignore
        }
    }
}
