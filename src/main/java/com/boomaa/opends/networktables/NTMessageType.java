package com.boomaa.opends.networktables;

public enum NTMessageType {
    kKeepAlive(0x00),
    kClientHello(0x01),
    kProtoUnsup(0x02),
    kServerHelloDone(0x03),
    kServerHello(0x04),
    kClientHelloDone(0x05),
    kEntryAssign(0x10),
    kEntryUpdate(0x11),
    kFlagsUpdate(0x12),
    kEntryDelete(0x13),
    kClearEntries(0x14),
    kExecuteRpc(0x20),
    kRpcResponse(0x21);

    private final int flag;

    NTMessageType(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static NTMessageType getFromFlag(int flag) {
        for (NTMessageType type : NTMessageType.values()) {
            if (type.flag == flag) {
                return type;
            }
        }
        return null;
    }
}
