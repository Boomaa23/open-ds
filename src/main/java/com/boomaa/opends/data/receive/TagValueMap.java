package com.boomaa.opends.data.receive;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TagValueMap<T> extends HashMap<String, T> {
    protected ReceiveTag baseTag;

    public TagValueMap<T> addTo(String key, T value) {
        super.put(key, value);
        return this;
    }

    public String toLogString(boolean addTimestamp) {
        StringBuilder sb = new StringBuilder();
        if (addTimestamp) {
            sb.append(Calendar.getInstance().getTime().toString()).append("> ");
        }
        for (Map.Entry<String, T> entry : this.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public TagValueMap<T> setBaseTag(ReceiveTag baseTag) {
        this.baseTag = baseTag;
        return this;
    }

    public ReceiveTag getBaseTag() {
        return baseTag;
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
