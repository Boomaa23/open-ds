package com.boomaa.opends.data.holders;

public enum Trace {
    ROBOTCODE(0x20),
    ISROBORIO(0x10),
    TESTMODE(0x08),
    AUTOMODE(0x04),
    TELEOPCODE(0x02),
    DISABLED(0x01);

    private final byte value;

    Trace(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
