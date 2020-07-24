package com.boomaa.opends.data.holders;

public enum Control {
    ESTOP(0x80, 0),
    FMS_CONNECTED(0x08, 4),
    ENABLED(0x04, 5),
    TELEOP_MODE(0x00, 6, 7),
    TEST_MODE(0x01, 6, 7),
    AUTO_MODE(0x02, 6, 7);

    private final byte flag;
    private final int[] bitmaskPos;

    Control(int flag, int... bitmaskPos) {
        this.flag = (byte) flag;
        this.bitmaskPos = bitmaskPos;
    }

    public byte getFlag() {
        return flag;
    }

    public int[] getBitmaskPos() {
        return bitmaskPos;
    }
}
