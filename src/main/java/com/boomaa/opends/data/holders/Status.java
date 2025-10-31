package com.boomaa.opends.data.holders;

public enum Status implements DataBase.Holder {
    ESTOP(DataBase.create()
            .addFlag(0x80, 2024, 2023, 2022, 2021, 2020)
    ),
    BROWNOUT(DataBase.create()
            .addFlag(0x10, 2024, 2023, 2022, 2021, 2020)
    ),
    CODE_INIT(DataBase.create()
            .addFlag(0x08, 2024, 2023, 2022, 2021, 2020)
            .addFlag(0x01, 2016, 2015)
    ),
    ENABLED(DataBase.create()
            .addFlag(0x04, 2024, 2023, 2022, 2021, 2020)
    ),
    TELEOP_MODE(DataBase.create()
            .addFlag(0x00, 2024, 2023, 2022, 2021, 2020)
    ),
    TEST_MODE(DataBase.create()
            .addFlag(0x01, 2024, 2023, 2022, 2021, 2020)
    ),
    AUTO_MODE(DataBase.create()
            .addFlag(0x02, 2024, 2023, 2022, 2021, 2020)
    );

    public final DataBase flags;

    Status(DataBase flags) {
        this.flags = flags;
    }

    @Override
    public int getFlag() {
        return flags.getCurrentFlag();
    }
}
