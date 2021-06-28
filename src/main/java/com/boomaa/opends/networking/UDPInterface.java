package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.*;

public class UDPInterface {
    private DatagramSocket clientSocket;
    private DatagramSocket serverSocket;
    private InetAddress ip;
    private int clientPort;
    private int bufSize = 1024;
    private boolean closed;

    public UDPInterface(String clientIp, int clientPort, int serverPort) throws SocketException {
        try {
            this.ip = InetAddress.getByName(clientIp);
            this.clientPort = clientPort;
            this.clientSocket = new DatagramSocket();
            this.serverSocket = new DatagramSocket(serverPort);
            clientSocket.setSoTimeout(1000);
            serverSocket.setSoTimeout(1000);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void doSend(byte[] data) {
        if (!closed) {
            doSendAssumedOpen(data);
        }
    }

    protected void doSendAssumedOpen(byte[] data) {
        try {
            clientSocket.send(new DatagramPacket(data, data.length, ip, clientPort));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] doReceieve() {
        byte[] buffer = new byte[bufSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            if (closed) {
                return new byte[0];
            }
            serverSocket.receive(packet);
        } catch (SocketTimeoutException | SocketException e) {
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

    public void close() {
        closed = true;
        clientSocket.close();
        serverSocket.close();
    }

    public boolean isClosed() {
        return closed;
    }
}
