package com.boomaa.opends.networking;

import java.net.DatagramPacket;

public class UDPTransform {
    private final byte[] buffer;
    private final DatagramPacket packet;

    public UDPTransform(byte[] buffer, DatagramPacket packet) {
        this.buffer = buffer;
        this.packet = packet;
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
}
