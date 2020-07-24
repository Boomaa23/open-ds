package com.boomaa.opends.util;

public class SequenceCounter {
    private short counter = 0;

    public SequenceCounter increment() {
        counter++;
        return this;
    }

    public byte[] getBytes() {
        return new byte[] { (byte) (counter >> 8), (byte) counter };
    }
}
