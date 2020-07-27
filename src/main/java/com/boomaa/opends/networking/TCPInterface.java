package com.boomaa.opends.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class TCPInterface {
    private Socket socket;
    private BufferedReader in;
    private boolean closed;

    public TCPInterface(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] doInteract(byte[] data) {
        try {
            socket.getOutputStream().write(data);
            return in.readLine().getBytes();
        } catch (SocketException e) {
            return new byte[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        this.closed = true;
        try {
            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
