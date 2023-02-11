package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class UDPInterface implements NetworkInterface {
    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;
    private InetAddress ip;
    private int clientPort;
    private int bufSize = 1024;
    private boolean closed;

    public UDPInterface(String clientIp, int clientPort, int serverPort, int timeout) throws SocketException {
        try {
            this.ip = InetAddress.getByName(clientIp);
            this.clientPort = clientPort;
            this.clientSocket = new DatagramSocket();
            this.serverSocket = new DatagramSocket(serverPort);
            if (timeout != -1) {
                clientSocket.setSoTimeout(timeout);
                serverSocket.setSoTimeout(timeout);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public UDPInterface(String clientIp, int clientPort, int serverPort) throws SocketException {
        this(clientIp, clientPort, serverPort, 2000);
    }

    @Override
    public boolean write(byte[] data) {
        if (!closed) {
            try {
                clientSocket.send(new DatagramPacket(data, data.length, ip, clientPort));
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
            serverSocket.receive(packet);
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
        clientSocket.close();
        serverSocket.close();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return ip.getHostName() + ":RX" + serverSocket.getLocalPort() + "/TX" + clientPort;
    }
}
