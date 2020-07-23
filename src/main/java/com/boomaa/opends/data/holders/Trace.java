package com.boomaa.opends.data.holders;

public enum Trace {
    ROBOTCODE(0x20, 2),
    ISROBORIO(0x10, 3),
    TESTMODE(0x08, 4),
    AUTOMODE(0x04, 5),
    TELEOPCODE(0x02, 6),
    DISABLED(0x01, 7);

    private final byte flag;
    private final int bitmaskPos;

    Trace(int flag, int bitmaskPos) {
        this.flag = (byte) flag;
        this.bitmaskPos = bitmaskPos;
    }

    public byte getFlag() {
        return flag;
    }

    public int getBitmaskPos() {
        return bitmaskPos;
    }
}
