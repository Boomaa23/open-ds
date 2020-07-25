package com.boomaa.opends.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class TCPServer extends Thread {
    private ServerSocket socket;
    private boolean closed = false;

    public TCPServer(int port) {
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void onRun(Socket client, OutputStream out, byte[] data);

    @Override
    public void run() {
        super.run();
        while (!closed) {
            try {
                Socket client = socket.accept();
                onRun(client, client.getOutputStream(), client.getInputStream().readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void close() {
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
