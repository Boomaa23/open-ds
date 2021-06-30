package com.boomaa.opends.usb;

public class IOKitEvent {
    private final long type;
    private final long cookie;
    private final int value;

    public IOKitEvent(long type, long cookie, int value) {
        this.type = type;
        this.cookie = cookie;
        this.value = value;
    }

    public long getType() {
        return type;
    }

    public long getCookie() {
        return cookie;
    }

    public int getValue() {
        return value;
    }
}
