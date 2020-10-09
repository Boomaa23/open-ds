package com.boomaa.opends.data.holders;

import com.boomaa.opends.display.MainJDEC;

public enum Status implements DataBase {
    ESTOP(0x80),
    BROWNOUT(0x10),
    CODE_INIT(0x08, 0x01, 0x01),
    ENABLED(0x04),
    TELEOP_MODE(0x00),
    TEST_MODE(0x01),
    AUTO_MODE(0x02);

    private final int[] flags;

    Status(int... flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags[MainJDEC.getProtocolIndex()];
    }
}
