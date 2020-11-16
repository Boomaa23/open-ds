package com.boomaa.opends.networktables;

import java.util.ArrayList;
import java.util.List;

public class NTEntry {
    private static final List<String> shouldUpdate = new ArrayList<>();
    private final String path;
    private final int id;
    private final String key;
    private String tabName;
    private final boolean inShuffleboard;
    private final boolean inSmartDashboard;
    private final NTDataType dataType;
    private final boolean inMetadata;
    private Object value;

    public NTEntry(String path, int id, NTDataType dataType, Object value) {
        this.path = path;
        this.id = id;
        this.inShuffleboard = path.contains("Shuffleboard");
        this.inSmartDashboard = path.contains("Smart Dashboard");
        this.dataType = dataType;
        int ioFirstSep = path.indexOf("/");
        int ioSecSep = path.indexOf("/",  ioFirstSep + 1);
        int ioLastSep = path.indexOf("/", (ioSecSep == -1 ? ioFirstSep : ioSecSep) + 1);
        if (ioLastSep != -1) {
            this.key = path.substring(ioLastSep + 1);
            this.tabName = path.substring(ioSecSep + 1, ioLastSep);
        } else {
            this.key = path.substring(ioSecSep + 1);
            this.tabName = path.substring(1, ioSecSep);
        }
        if (inSmartDashboard) {
            this.tabName = "Smart Dashboard";
        }
        this.inMetadata = key.startsWith(".") || path.contains("CameraPublisher");
        if (!NTStorage.TABS.contains(tabName) && !inMetadata && !tabName.startsWith(".")) {
            NTStorage.TABS.add(tabName);
        }
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
        shouldUpdate.add(tabName);
        this.value = value;
    }

    public boolean isInShuffleboard() {
        return inShuffleboard;
    }

    public boolean isInSmartDashboard() {
        return inSmartDashboard;
    }

    public boolean isInMetadata() {
        return inMetadata;
    }

    public String getTabName() {
        return tabName;
    }

    public NTDataType getDataType() {
        return dataType;
    }

    public static List<String> getShouldUpdate() {
        return shouldUpdate;
    }
}
