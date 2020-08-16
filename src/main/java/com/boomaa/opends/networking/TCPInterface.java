package com.boomaa.opends.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TCPInterface {
    private Socket socket;
    private BufferedReader in;
    private boolean closed;

    public TCPInterface(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socket.setSoTimeout(100);
        } catch (ConnectException e) {
            this.closed = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] doInteract(byte[] data) {
        try {
            while (socket == null || in == null) {
                Thread.sleep(50);
            }
            socket.getOutputStream().write(data);
            String out = in.readLine();
            return out != null ? out.getBytes() : null;
        } catch (SocketException e) {
            return null;
        } catch (SocketTimeoutException e) {
            return new byte[0];
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        this.closed = true;
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
