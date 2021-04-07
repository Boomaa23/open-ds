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

    public TCPInterface(String ip, int port) throws SocketException {
        try {
            this.socket = new Socket(ip, port);
            this.in = socket.getInputStream();
        } catch (ConnectException e) {
            close();
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        if (socket == null || in == null) {
            close();
            throw new SocketException("Null socket or socket input stream");
        }
    }

    public byte[] read() throws IOException {
        //TODO change to 1500 (ethernet max MTU) for non-testing
        byte[] out = new byte[65535];
        int numRead = in.read(out);
        if (numRead == -1) {
            numRead = out.length;
        }
        out = ArrayUtils.sliceArr(out, 0, numRead);
        return out;
    }

    public byte[] doInteract(byte[] data) {
        try {
            socket.getOutputStream().write(data);
            return read();
        } catch (SocketException e) {
            close();
            return null;
        } catch (SocketTimeoutException e) {
            close();
            return new byte[0];
        } catch (IOException e) {
            close();
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
