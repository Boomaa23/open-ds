package com.boomaa.opends.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPInterface {
    public static class Send {
        private DatagramSocket socket;
        private InetAddress ip;
        private int port;

        public Send(String ip, int port) {
            try {
                this.ip = InetAddress.getByName(ip);
                this.port = port;
                this.socket = new DatagramSocket();
            } catch (SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public void doSend(byte[] data) {
            try {
                socket.send(new DatagramPacket(data, data.length, ip, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Receive {
        private DatagramSocket socket;
        private int bufSize = 1024;

        public Receive(int port) {
            try {
                this.socket = new DatagramSocket(port);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        public UDPTransform doReceieve() {
            byte[] buffer = new byte[bufSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new UDPTransform(buffer, packet);
        }

        public void setBufSize(int bufSize) {
            this.bufSize = bufSize;
        }
    }
}
