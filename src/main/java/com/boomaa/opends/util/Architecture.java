package com.boomaa.opends.util;

public enum Architecture {
    AMD64("amd64", "x86_64"),
    AARCH64("aarch64", "aarch64e");

    private final String[] archNames;

    Architecture(String... archNames) {
        this.archNames = archNames;
    }

    public static Architecture getCurrent() {
        String search = System.getProperty("os.arch").toLowerCase();
        for (Architecture arch : Architecture.values()) {
            for (String name : arch.archNames) {
                if (search.contains(name)) {
                    return arch;
                }
            }
        }
        throw new NativeSystemError("Unsupported architecture " + search);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
