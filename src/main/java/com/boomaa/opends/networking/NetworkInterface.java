package com.boomaa.opends.networking;

public interface NetworkInterface {
    void write(byte[] data);

    byte[] read();

    void close();

    boolean isClosed();
}
