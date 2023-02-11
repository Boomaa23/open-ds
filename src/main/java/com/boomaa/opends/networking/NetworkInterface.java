package com.boomaa.opends.networking;

public interface NetworkInterface {
    boolean write(byte[] data);

    byte[] read();

    void close();

    boolean isClosed();
}
