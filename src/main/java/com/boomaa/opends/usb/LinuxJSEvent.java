package com.boomaa.opends.usb;

public class LinuxJSEvent {
    private final int value;
    private final byte type;
    private final int number;

    public LinuxJSEvent(int value, byte type, int number) {
        this.value = value;
        this.type = type;
        this.number = number;
    }

    public int getValue() {
        return value;
    }

    public byte getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    public boolean isValid() {
        return value != -1 || type != -1 || number != -1;
    }
}
