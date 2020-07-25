package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new UDPTransform(buffer, packet);
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }
}
