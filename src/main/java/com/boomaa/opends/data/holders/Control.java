package com.boomaa.opends.data.holders;

import com.boomaa.opends.display.MainJDEC;

public enum Control implements DataBase {
    ESTOP(0x80, 0x80, 0x80, 0x00),
    FMS_CONNECTED(0x08, 0x08, 0x08, 0x08),
    ENABLED(0x04, 0x04, 0x04, 0x20),
    TELEOP_MODE(0x00, 0x00, 0x00, 0x00),
    TEST_MODE(0x01, 0x01, 0x01, 0x02),
    AUTO_MODE(0x02, 0x02, 0x02, 0x10);

    private final int[] flags;

    Control(int... flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags[MainJDEC.getProtocolIndex()];
    }
}
