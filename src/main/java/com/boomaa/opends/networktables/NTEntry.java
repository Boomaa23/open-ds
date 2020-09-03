package com.boomaa.opends.networktables;

public class NTEntry {
    private final String path;
    private final int id;
    private final String key;
    private Object value;

    public NTEntry(String path, int id, String key, Object value) {
        this.path = path;
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
