package com.boomaa.opends.data.holders;

public enum Status {
    ESTOP(0x80),
    BROWNOUT(0x10),
    CODE_INIT(0x08),
    ENABLED(0x04),
    TELEOP_MODE(0x00),
    TEST_MODE(0x01),
    AUTO_MODE(0x02);

    private final byte value;

    Status(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
