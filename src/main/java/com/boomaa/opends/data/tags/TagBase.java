package com.boomaa.opends.data.tags;

public interface TagBase<T> {
    TagValueMap<T> getValue(byte[] packet, int size);
}
