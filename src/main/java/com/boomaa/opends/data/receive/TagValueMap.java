package com.boomaa.opends.data.receive;

import java.util.HashMap;

public class TagValueMap<T> extends HashMap<String, T> {
    public TagValueMap<T> addTo(String key, T value) {
        super.put(key, value);
        return this;
    }

    public static <K> TagValueMap<K> singleton(String key, K value) {
        return new TagValueMap<K>().addTo(key, value);
    }

    public static TagValueMap<Byte> passPackets(byte[] packet, int size) {
        TagValueMap<Byte> map = new TagValueMap<>();
        for (int i = 0; i < packet.length; i++) {
            map.put("byte_seq_" + i, packet[i]);
        }
        return map;
    }
}
