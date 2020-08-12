package com.boomaa.opends.util;

public class SequenceCounter {
    private final boolean isRoundTrip;
    private short counter;

    public SequenceCounter(boolean isRoundTrip, int offset) {
        this.isRoundTrip = isRoundTrip;
        this.counter = (short) ((isRoundTrip ? -2 : -1) + offset);
    }

    public SequenceCounter(boolean isRoundTrip) {
        this(isRoundTrip, 0);
    }

    public SequenceCounter increment() {
        counter += isRoundTrip ? 2 : 1;
        return this;
    }

    public byte[] getBytes() {
        return new byte[] { (byte) (counter >> 8), (byte) counter };
    }

    public short getCounter() {
        return counter;
    }
}
