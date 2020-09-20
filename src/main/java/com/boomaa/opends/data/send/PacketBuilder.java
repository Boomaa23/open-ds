package com.boomaa.opends.data.send;

import java.util.ArrayList;
import java.util.List;

public class PacketBuilder {
    protected final List<Byte> packet = new ArrayList<>();

    public PacketBuilder() {
    }

    public PacketBuilder(byte... init) {
        addBytes(init);
    }

    public PacketBuilder addInt(int i) {
        packet.add((byte) i);
        return this;
    }

    public PacketBuilder addInts(int... ints) {
        for (int i : ints) {
            packet.add((byte) i);
        }
        return this;
    }

    public PacketBuilder addByte(byte b) {
        packet.add(b);
        return this;
    }

    public PacketBuilder addBytes(byte... bytes) {
        for (byte b : bytes) {
            packet.add(b);
        }
        return this;
    }

    public PacketBuilder pad(int value, int num) {
        for (int i = 0; i < num; i++) {
            addInt(value);
        }
        return this;
    }

    public int size() {
        return packet.size();
    }

    public byte[] build() {
        byte[] out = new byte[packet.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = packet.get(i);
        }
        return out;
    }
}
