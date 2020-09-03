package com.boomaa.opends.networking;

public class PortQuad extends PortTriple {
    private final int shuffleboard;

    public PortQuad(int tcp, int udpClient, int udpServer, int shuffleboard) {
        super(tcp, udpClient, udpServer);
        this.shuffleboard = shuffleboard;
    }

    public int getShuffleboard() {
        return shuffleboard;
    }
}
