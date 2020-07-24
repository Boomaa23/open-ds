package com.boomaa.opends.data.holders;

public enum Request {
    REBOOT_ROBO_RIO(0x08, 4),
    RESTART_CODE(0x04, 5);

    private final byte flag;
    private final int bitmaskPos;

    Request(int flag, int bitmaskPos) {
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
