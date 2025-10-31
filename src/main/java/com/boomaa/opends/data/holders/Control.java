package com.boomaa.opends.data.holders;

public enum Control implements DataBase.Holder {
    ESTOP(DataBase.create()
            .addFlag(0x80, 2024, 2023, 2022, 2021, 2020, 2016, 2015)
            .addFlag(0x00, 2014)
    ),
    FMS_CONNECTED(DataBase.create()
            .addFlag(0x08, 2024, 2023, 2022, 2021, 2020, 2016, 2015, 2014)
    ),
    ENABLED(DataBase.create()
            .addFlag(0x04, 2024, 2023, 2022, 2021, 2020, 2016, 2015)
            .addFlag(0x20, 2014)
    ),
    TELEOP_MODE(DataBase.create()
            .addFlag(0x00, 2024, 2023, 2022, 2021, 2020, 2016, 2015, 2014)
    ),
    TEST_MODE(DataBase.create()
            .addFlag(0x01, 2024, 2023, 2022, 2021, 2020, 2016, 2015)
            .addFlag(0x02, 2014)
    ),
    AUTO_MODE(DataBase.create()
            .addFlag(0x02, 2024, 2023, 2022, 2021, 2020, 2016, 2015)
            .addFlag(0x10, 2014)
    );

    public final DataBase flags;

    Control(DataBase flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags.getCurrentFlag();
    }
}
