package com.boomaa.opends.util;

public enum OperatingSystem {
    WINDOWS("win"),
    MACOS("mac"),
    UNIX("nix"),
    UNSUPPORTED("");

    private final String key;

    OperatingSystem(String key) {
        this.key = key;
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
}
