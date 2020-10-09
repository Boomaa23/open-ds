package com.boomaa.opends.data.holders;

import com.boomaa.opends.display.MainJDEC;

public enum Request implements DataBase {
    REBOOT_ROBO_RIO(0x08, -1, -1, 0x80),
    RESTART_CODE(0x04, -1, -1, -1);

    private final int[] flags;

    Request(int... flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags[MainJDEC.getProtocolIndex()];
    }
}
