package com.boomaa.opends.networking;

import com.boomaa.opends.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TCPInterface {
    private Socket socket;
    private InputStream in;
    private boolean closed;

    public TCPInterface(String ip, int port) {
        try {
            while (this.socket == null) {
                this.socket = new Socket(ip, port);
            }
            this.in = socket.getInputStream();
            socket.setSoTimeout(100);
        } catch (ConnectException e) {
            this.closed = true;
        } catch (IOException e) {
            this.closed = true;
            e.printStackTrace();
        }
    }

    public byte[] read() throws IOException {
        //TODO change to 1500 (ethernet max MTU) for non-testing
        byte[] out = new byte[65535];
        int numRead = in.read(out);
        out = ArrayUtils.sliceArr(out, 0, numRead);
        return out;
    }

    public byte[] doInteract(byte[] data) {
        try {
            while (socket == null || in == null) {
                Thread.sleep(50);
            }
            socket.getOutputStream().write(data);
            return read();
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
