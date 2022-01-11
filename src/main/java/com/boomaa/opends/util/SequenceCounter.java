package com.boomaa.opends.util;

public class SequenceCounter {
    private final boolean isRoundTrip;
    private final int offset;
    private final boolean isNullCounter;
    private short counter;

    public SequenceCounter(boolean isRoundTrip, int offset, boolean isNullCounter) {
        this.isRoundTrip = isRoundTrip;
        this.offset = offset;
        this.isNullCounter = isNullCounter;
        reset();
    }

    public SequenceCounter(boolean isRoundTrip) {
        this(isRoundTrip, 0, false);
    }

    public SequenceCounter increment() {
        if (!isNullCounter) {
            counter += isRoundTrip ? 2 : 1;
        }
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
