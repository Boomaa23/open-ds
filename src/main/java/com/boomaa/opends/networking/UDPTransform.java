package com.boomaa.opends.networking;

import java.net.DatagramPacket;

public class UDPTransform {
    private final byte[] buffer;
    private final DatagramPacket packet;
    private final boolean blank;

    public UDPTransform(byte[] buffer, DatagramPacket packet, boolean blank) {
        this.buffer = buffer;
        this.packet = packet;
        this.blank = blank;
    }

    public String asString() {
        return new String(buffer, 0, packet.getLength());
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    public boolean isBlank() {
        return blank;
    }
}
