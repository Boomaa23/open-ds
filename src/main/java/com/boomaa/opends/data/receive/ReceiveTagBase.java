package com.boomaa.opends.data.receive;

public interface ReceiveTagBase<T> {
    TagValueMap<T> getValue(byte[] packet, int size);

    default TagValueMap<T> getValue(byte[] packet) {
        return getValue(packet, -1);
    }
}
