package com.boomaa.opends.networking;

public class PortTriple {
    private final int tcp;
    private final int udpClient;
    private final int udpServer;

    public PortTriple(int tcp, int udpClient, int udpServer) {
        this.tcp = tcp;
        this.udpClient = udpClient;
        this.udpServer = udpServer;
    }

    public int getTcp() {
        return tcp;
    }

    public int getUdpClient() {
        return udpClient;
    }

    public int getUdpServer() {
        return udpServer;
    }
}
