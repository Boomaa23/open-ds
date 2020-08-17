package com.boomaa.opends.util.battery;

public enum BatteryFill {
    FULL(-1),
    HIGH(1),
    NORMAL(-1),
    LOW(2),
    CRITICAL(4),
    CHARGING(8),
    NONE(128),
    UNKNOWN(255);

    private final int value;

    BatteryFill(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
