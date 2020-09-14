package com.boomaa.opends.networktables;

public class NTEntry {
    private final String path;
    private final int id;
    private final String key;
    private final String tabName;
    private final boolean inShuffleboard;
    private final boolean inSmartDashboard;
    private Object value;

    public NTEntry(String path, int id, Object value) {
        this.path = path;
        this.id = id;
        int ioSecSep = path.indexOf("/", path.indexOf("/") + 1);
        int ioThirdSep = path.indexOf("/", ioSecSep + 1);
        this.key = path.substring(ioThirdSep + 1);
        this.tabName = path.substring(ioSecSep + 1, ioThirdSep);
        if (!NTStorage.TABS.contains(tabName)) {
            NTStorage.TABS.add(tabName);
        }
        this.inShuffleboard = path.contains("Shuffleboard");
        this.inSmartDashboard = path.contains("Smart Dashboard");
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

    public boolean isInShuffleboard() {
        return inShuffleboard;
    }

    public boolean isInSmartDashboard() {
        return inSmartDashboard;
    }

    public String getTabName() {
        return tabName;
    }
}
