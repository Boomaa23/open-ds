package com.boomaa.opends.networktables;

import com.boomaa.opends.display.frames.MainFrame;

public class NTEntry {
    private final String path;
    private final int id;
    private final String key;
    private final String tabName;
    private final boolean inShuffleboard;
    private final boolean inSmartDashboard;
    private final boolean inLiveWindow;
    private final NTDataType dataType;
    private final boolean inHidden;
    private Object value;
    private boolean persistent;

    public NTEntry(String path, int id, NTDataType dataType, Object value, boolean persistent) {
        this.path = path;
        this.id = id;
        this.value = value;
        this.inShuffleboard = path.contains("Shuffleboard");
        this.inSmartDashboard = path.contains("SmartDashboard");
        this.inLiveWindow = path.contains("LiveWindow");
        this.dataType = dataType;
        this.persistent = persistent;
        int ioSep = path.indexOf('/', 1);
        if (inShuffleboard) {
            int ioSep2 = path.indexOf('/', ioSep + 1);
            this.tabName = path.substring(ioSep + 1, ioSep2);
            this.key = path.substring(ioSep2 + 1);
        } else {
            this.tabName = path.substring(1, ioSep);
            this.key = path.substring(ioSep + 1);
        }
        this.inHidden = key.startsWith(".") || path.contains("CameraPublisher") || tabName.equals("FMSInfo") || path.contains("SendableChooser");
        if (!NTStorage.TABS.contains(tabName) && !inHidden && !tabName.startsWith(".")) {
            NTStorage.TABS.add(tabName);
            if (MainFrame.NT_FRAME != null) {
                MainFrame.NT_FRAME.populateTabsBar();
            }
        }
        displayQueue(this);
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
        displayQueue(this);
    }

    public boolean isInShuffleboard() {
        return inShuffleboard;
    }

    public boolean isInSmartDashboard() {
        return inSmartDashboard;
    }

    public boolean isInLiveWindow() {
        return inLiveWindow;
    }

    public boolean isInHidden() {
        return inHidden;
    }

    public String getTabName() {
        return tabName;
    }

    public NTDataType getDataType() {
        return dataType;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public static void displayQueue(NTEntry entry) {
        if (MainFrame.NT_FRAME != null) {
            MainFrame.NT_FRAME.updateValue(entry);
        }
    }
}
