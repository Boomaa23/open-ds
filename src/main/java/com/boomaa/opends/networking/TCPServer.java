package com.boomaa.opends.networking;

import com.boomaa.opends.display.frames.ErrorBox;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

@Deprecated
public abstract class TCPServer extends Thread {
    protected ServerSocket socket;
    protected boolean closed = false;

    public TCPServer(int port) {
        try {
            this.socket = new ServerSocket(port);
        } catch (BindException e) {
            ErrorBox.show(e.getMessage());
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
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return closed;
    }
}
