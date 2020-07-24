package com.boomaa.opends.data.receive;

public interface ReceiveTagBase<T> {
    TagValueMap<T> getValue(byte[] packet, int size);
}
