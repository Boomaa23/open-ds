package com.boomaa.opends.data.holders;

public enum Trace implements DataBase.Holder {
    ROBOTCODE(DataBase.create()
            .addFlag(0x20, 2021, 2020)
    ),
    ISROBORIO(DataBase.create()
            .addFlag(0x10, 2021, 2020)
    ),
    TESTMODE(DataBase.create()
            .addFlag(0x08, 2021, 2020)
    ),
    AUTOMODE(DataBase.create()
            .addFlag(0x04, 2021, 2020)
    ),
    TELEOPCODE(DataBase.create()
            .addFlag(0x02, 2021, 2020)
    ),
    DISABLED(DataBase.create()
            .addFlag(0x01, 2021, 2020)
    );

    public final DataBase flags;

    Trace(DataBase flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags.getCurrentFlag();
    }
}
