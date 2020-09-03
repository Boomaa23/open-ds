package com.boomaa.opends.networktables;

public enum NTDataType {
    NT_BOOLEAN(0x00),
    NT_DOUBLE(0x01),
    NT_STRING(0x02),
    NT_RAW(0x03),
    NT_BOOLEAN_ARRAY(0x10),
    NT_DOUBLE_ARRAY(0x11),
    NT_STRING_ARRAY(0x12),
    NT_RPC(0x20);

    private final int flag;

    NTDataType(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static NTDataType getFromFlag(int flag) {
        for (NTDataType type : NTDataType.values()) {
            if (type.flag == flag) {
                return type;
            }
        }
        return null;
    }
}
