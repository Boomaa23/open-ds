package com.boomaa.opends.networking;

public class PortTriple {
    private final int tcp;
    private final int udpTx;
    private final int udpRx;

    public PortTriple(int tcp, int udpTx, int udpRx) {
        this.tcp = tcp;
        this.udpTx = udpTx;
        this.udpRx = udpRx;
    }

    public int getTcp() {
        return tcp;
    }

    public int getUdpTx() {
        return udpTx;
    }

    public int getUdpRx() {
        return udpRx;
    }
}
