package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPInterface implements NetworkInterface {
    private DatagramSocket txSocket;
    private DatagramSocket rxSocket;
    private InetAddress ip;
    private int txPort;
    private int bufSize = 1024;
    private boolean closed;

    public UDPInterface(String ip, int txPort, int rxPort, int timeout) throws SocketException {
        try {
            this.ip = InetAddress.getByName(ip);
            this.txPort = txPort;
            this.txSocket = new DatagramSocket();
            this.rxSocket = new DatagramSocket(rxPort);
            if (timeout != -1) {
                txSocket.setSoTimeout(timeout);
                rxSocket.setSoTimeout(timeout);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public UDPInterface(String ip, int txPort, int rxPort) throws SocketException {
        this(ip, txPort, rxPort, 2000);
    }

    @Override
    public boolean write(byte[] data) {
        if (!closed) {
            try {
                txSocket.send(new DatagramPacket(data, data.length, ip, txPort));
                return true;
            } catch (IOException e) {
                close();
            }
        }
        return false;
    }

    @Override
    public byte[] read() {
        byte[] buffer = new byte[bufSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            if (closed) {
                return new byte[0];
            }
            rxSocket.receive(packet);
        } catch (SocketTimeoutException e) {
            return new byte[0];
        } catch (SocketException e) {
            close();
            return new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    @Override
    public void close() {
        closed = true;
        txSocket.close();
        rxSocket.close();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return ip.getHostName() + ":RX" + rxSocket.getLocalPort() + "/TX" + txPort;
    }
}
