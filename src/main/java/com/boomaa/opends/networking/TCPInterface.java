package com.boomaa.opends.networking;

import com.boomaa.opends.util.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class TCPInterface implements NetworkInterface {
    private Socket socket;
    private InputStream in;
    private boolean closed;
    private final String ip;
    private final int port;

    public TCPInterface(String ip, int port, int timeout) throws SocketException {
        this.ip = ip;
        this.port = port;
        try {
            this.socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            this.in = socket.getInputStream();
            if (timeout != -1) {
                socket.setSoTimeout(timeout);
            }
        } catch (ConnectException e) {
            close();
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        if (socket == null) {
            close();
            throw new SocketException("Null socket");
        } else if (in == null) {
            close();
            throw new SocketException("Null socket input stream");
        }
    }

    public TCPInterface(String ip, int port) throws SocketException {
        this(ip, port, 1000);
    }

    @Override
    public byte[] read() {
        Object out = checkAction((v) -> internalRead(), null);
        return out != null ? (byte[]) out : null;
    }

    private byte[] internalRead() throws IOException {
        //TODO change to 1500 (ethernet max MTU) for non-testing
        byte[] out = new byte[65535];
        int numRead = in.read(out);
        if (numRead == -1) {
            numRead = out.length;
        }
        out = ArrayUtils.slice(out, 0, numRead);
        return out;
    }

    @Override
    public boolean write(byte[] data) {
        checkAction((d) -> {
            socket.getOutputStream().write(d);
            return null;
        }, data);
        return true;
    }

    public <T> Object checkAction(NetworkAction<T> func, T param) {
        try {
            return func.apply(param);
        } catch (SocketException e) {
            close();
            return null;
        } catch (SocketTimeoutException e) {
            return new byte[0];
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() {
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
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    private interface NetworkAction<T> {
        Object apply(T t) throws IOException;
    }
}
