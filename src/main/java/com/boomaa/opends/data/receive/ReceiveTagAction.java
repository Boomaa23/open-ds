package com.boomaa.opends.data.receive;

public interface ReceiveTagAction<T> {
    TagValueMap<T> getValue(byte[] packet, int size);

    default TagValueMap<T> getValue(byte[] packet) {
        return getValue(packet, -1);
    }
}
