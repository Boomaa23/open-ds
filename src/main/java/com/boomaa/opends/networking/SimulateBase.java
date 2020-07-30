package com.boomaa.opends.networking;

public class SimulateBase {
    protected final PortTriple ports;
    protected final TCPServer tcpAsRemote;
    protected final TCPInterface tcpAsDs;
    protected final UDPInterface udpAsRemote;
    protected final UDPInterface udpAsDs;

    public SimulateBase(PortTriple ports, TCPServer tcpAsRemote) {
        this.ports = ports;
        this.tcpAsRemote = tcpAsRemote;
        this.tcpAsDs = new TCPInterface(AddressConstants.LOCALHOST, ports.getTcp());
        this.udpAsRemote = new UDPInterface(AddressConstants.LOCALHOST, ports.getUdpServer(), ports.getUdpClient());
        this.udpAsDs = new UDPInterface(AddressConstants.LOCALHOST, ports.getUdpClient(), ports.getUdpServer());
    }

    public UDPTransform doLoopbackUDPSend(byte[] data) {
        udpAsDs.doSend(data);
        return udpAsRemote.doReceieve();
    }

    public byte[] doLoopbackTCPSend(byte[] data) {
        return tcpAsDs.doInteract(data);
    }

    public void close() {
        udpAsRemote.close();
        udpAsDs.close();
        tcpAsRemote.close();
        tcpAsDs.close();
    }

    public boolean isClosed() {
        return udpAsRemote.isClosed() && udpAsDs.isClosed()
                && tcpAsRemote.isClosed() && tcpAsDs.isClosed();
    }
}
