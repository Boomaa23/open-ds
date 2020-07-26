package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.*;

public class UDPInterface {
    private DatagramSocket clientSocket;
    private InetAddress ip;
    private int clientPort;
    private DatagramSocket serverSocket;
    private int bufSize = 1024;

    public UDPInterface(String clientIp, int clientPort, int serverPort) {
        try {
            this.ip = InetAddress.getByName(clientIp);
            this.clientPort = clientPort;
            this.clientSocket = new DatagramSocket();
            this.serverSocket = new DatagramSocket(serverPort);
            this.serverSocket.setSoTimeout(100);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void doSend(byte[] data) {
        try {
            clientSocket.send(new DatagramPacket(data, data.length, ip, clientPort));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UDPTransform doReceieve() {
        byte[] buffer = new byte[bufSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            serverSocket.receive(packet);
        } catch (SocketTimeoutException e) {
            return new UDPTransform(new byte[0], packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UDPTransform(buffer, packet);
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public void close() {
        clientSocket.close();
        serverSocket.close();
    }
}
