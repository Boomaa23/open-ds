package com.boomaa.opends.util;

public class SequenceCounter {
    private final boolean isRoundTrip;
    private final int offset;
    private short counter;

    public SequenceCounter(boolean isRoundTrip, int offset) {
        this.isRoundTrip = isRoundTrip;
        this.offset = offset;
        reset();
    }

    public SequenceCounter(boolean isRoundTrip) {
        this(isRoundTrip, 0);
    }

    public SequenceCounter increment() {
        counter += isRoundTrip ? 2 : 1;
        return this;
    }

    public SequenceCounter reset() {
        this.counter = (short) ((isRoundTrip ? 2 : 1) + offset);
        return this;
    }

    public byte[] getBytes() {
        return NumberUtils.intToBytePair(counter);
    }

    public short getCounter() {
        return counter;
    }
}
