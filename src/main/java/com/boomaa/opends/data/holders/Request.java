package com.boomaa.opends.data.holders;

public enum Request implements DataBase.Holder {
    // added, not in FRCture documentation
    DS_CONNECTED(DataBase.create()
            .addFlag(0x10, 2024, 2023, 2022, 2021, 2020)
    ),
    REBOOT_ROBO_RIO(DataBase.create()
            .addFlag(0x08, 2024, 2023, 2022, 2021, 2020)
            .addFlag(0x80, 2014)
    ),
    RESTART_CODE(DataBase.create()
            .addFlag(0x04, 2024, 2023, 2022, 2021, 2020)
    );

    public final DataBase flags;

    Request(DataBase flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags.getCurrentFlag();
    }
}
