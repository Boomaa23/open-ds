package com.boomaa.opends.networking;

public enum DataMap {
    // udp ds -> rio
    ESTOP(0x80),
    FMS_CONN(0x08),
    ENABLED(0x04),
    TELEOP_MODE(0x00),
    TEST_MODE(0x01),
    AUTO_MODE(0x02),
    RESTART_RIO(0x08),
    RESTART_CODE(0x04),

    COUNTDOWN_FLAG(0x07),
    JS_FLAG(0x0C),
    DATE_FLAG(0x0F),
    TIMEZONE_FLAG(0x10),

    // tcp ds -> rio
    JOYSTICK_DESC_FLAG(0x02),
    MATCH_INFO_FLAG(0x07),
    GAME_DATA_FLAG(0x0E),

    // udp rio -> ds
    // duplicate entry estop
    BROWNOUT(0x10),
    CODE_INIT(0x08),
    // duplicate entry enabled
    // duplicate entry mode (x3)
    TRACE_ROBOTCODE(0x20),
    TRACE_ISROBORIO(0x10),
    TRACE_TESTMODE(0x08),
    TRACE_AUTOMODE(0x04),
    TRACE_TELEOPCODE(0x02),
    TRACE_DISABLED(0x01),

    JS_OUTPUT_FLAG(0x01),
    DISK_INFO_FLAG(0x04),
    CPU_INFO_FLAG(0x05),
    RAM_INFO_FLAG(0x06),
    PDP_LOG_FLAG(0x08),
    UNKNOWN_UDP_FLAG(0x09),
    CANMETRICS_FLAG(0x0E),
    
    // tcp rio -> ds
    RADIO_ERROR_FLAG(0x00),
    USAGE_REPORT_FLAG(0x01),
    DISABLE_FAULT_FLAG(0x04),
    RAIL_FAULT_FLAG(0x05),
    VER_INFO_FLAG(0x0A),
    ERR_MSG_FLAG(0x0B),
    STD_OUT_FLAG(0x0C),
    UNKNOWN_TCP_FLAG(0x0D);

    //TODO fms

    private final byte flag;

    DataMap(int flag) {
        this.flag = (byte) flag;
    }

    public static byte getAlliance(boolean isBlue, int num) {
        return (byte) (num + (isBlue ? 3 : 0));
    }
}
