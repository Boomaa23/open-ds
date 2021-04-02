package com.boomaa.opends.data.holders;

import com.boomaa.opends.display.MainJDEC;

public enum Trace implements DataBase {
    ROBOTCODE(0x20, 0x20),
    ISROBORIO(0x10, 0x10),
    TESTMODE(0x08, 0x08),
    AUTOMODE(0x04, 0x04),
    TELEOPCODE(0x02, 0x02),
    DISABLED(0x01, 0x01);

    private final int[] flags;

    Trace(int... flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags[MainJDEC.getProtocolIndex()];
    }
}
