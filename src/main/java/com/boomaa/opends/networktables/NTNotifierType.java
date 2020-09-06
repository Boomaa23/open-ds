package com.boomaa.opends.networktables;

@Deprecated
public enum NTNotifierType {
    NT_NOTIFY_NONE(0x00),
    NT_NOTIFY_IMMEDIATE(0x01), // initial listener addition
    NT_NOTIFY_LOCAL(0x02), // changed locally
    NT_NOTIFY_NEW(0x04), // newly created entry
    NT_NOTIFY_DELETE(0x08), // deleted
    NT_NOTIFY_UPDATE(0x10), // value changed
    NT_NOTIFY_FLAGS(0x20); // flags changed

    private final int flag;

    NTNotifierType(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
