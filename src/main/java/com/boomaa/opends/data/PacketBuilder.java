package com.boomaa.opends.data;

import java.util.ArrayList;
import java.util.List;

public class PacketBuilder {
    private final List<Byte> packet = new ArrayList<>();

    public PacketBuilder() {
    }

    public PacketBuilder(byte... init) {
        for (byte b : init) {
            packet.add(b);
        }
    }

    public void addValue(byte b) {
        packet.add(b);
    }

    public byte[] build() {
        byte[] out = new byte[packet.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = packet.get(i);
        }
        return out;
    }
}
