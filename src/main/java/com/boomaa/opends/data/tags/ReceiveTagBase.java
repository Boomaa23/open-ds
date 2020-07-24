package com.boomaa.opends.data.tags;

public interface ReceiveTagBase<T> {
    TagValueMap<T> getValue(byte[] packet, int size);
}
