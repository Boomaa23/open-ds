package com.boomaa.opends.util;

public enum Architecture {
    AMD64,
    AARCH64;

    public static Architecture getCurrent() {
        String search = System.getProperty("os.arch").toLowerCase();
        for (Architecture arch : Architecture.values()) {
            if (search.contains(arch.toString())) {
                return arch;
            }
        }
        throw new NativeSystemError("Unsupported architecture " + search);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
