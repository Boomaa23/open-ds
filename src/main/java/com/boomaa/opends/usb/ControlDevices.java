package com.boomaa.opends.usb;

import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.util.Debug;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControlDevices {
    private static final Map<Integer, HIDDevice> controllers = new LinkedHashMap<>();
    private static int sendDataCtr = 0;
    private static int sendDescCtr = 0;

    private ControlDevices() {
    }

    public static void init() {
        findAll();
        updateValues();
        Debug.println("Human input/control devices initialized");
    }

    public static synchronized void findAll() {
        NativeUSBManager.getOSInstance().enumDevices();
        for (Controller<?> ctrl : NativeUSBManager.getOSInstance().getDevices()) {
            boolean hasHid = false;
            for (HIDDevice hid : controllers.values()) {
                if (hid.hasController(ctrl)) {
                    hasHid = true;
                    break;
                }
            }
            if (!hasHid) {
                HIDDevice hid = new HIDDevice(ctrl);
                controllers.put(hid.getIdx(), hid);
                final int addIdx = Math.min(hid.getIdx(), JoystickTab.EmbeddedJDEC.LIST_MODEL.size());
                JoystickTab.EmbeddedJDEC.LIST_MODEL.add(addIdx, hid);
                Debug.println(String.format("Found and added new HID/controller device (%s) with %d buttons and %d axes",
                    hid, hid.deviceNumButtons(), hid.deviceNumAxes()));
            }
        }
    }

    public static synchronized void checkForRemoval() {
        for (Controller<?> ctrl : NativeUSBManager.getOSInstance().getDevices()) {
            if (ctrl.needsRemove()) {
                NativeUSBManager.getOSInstance().getDevices().remove(ctrl);
            }
        }
        for (HIDDevice hid : controllers.values()) {
            if (hid.needsRemove()) {
                controllers.remove(hid.getIdx());
                IndexTracker.unregister(hid.getIdx());
                final int remIdx = Math.min(hid.getIdx(), JoystickTab.EmbeddedJDEC.LIST_MODEL.size() - 1);
                JoystickTab.EmbeddedJDEC.LIST_MODEL.remove(remIdx);
                Debug.println(String.format("Removed now invalid HID/controller device: %s", hid));
            }
        }
    }

    public static synchronized void reindexAll() {
        Debug.println("Device user indexing has changed - reindexing controller list");
        Map<Integer, HIDDevice> deviceMapTemp = new HashMap<>(controllers);
        controllers.clear();
        for (HIDDevice device : deviceMapTemp.values()) {
            int devIdx = device.getIdx();
            controllers.put(devIdx, device);
        }
    }

    public static synchronized void clearAll() {
        Debug.println("Removed all control devices");
        NativeUSBManager.getOSInstance().clearDevices();
        IndexTracker.reset();
        controllers.clear();
    }

    public static synchronized void updateValues() {
        for (HIDDevice hid : controllers.values()) {
            hid.update();
        }
    }

    public static synchronized int iterateSend(boolean isData) {
        int out;
        if (isData) {
            out = sendDataCtr++;
            sendDataCtr %= (IndexTracker.MAX_JS_INDEX + 1);
        } else {
            out = sendDescCtr++;
            sendDescCtr %= IndexTracker.MAX_JS_NUM;
        }
        return out;
    }

    public static int getDataIndex() {
        return sendDataCtr;
    }

    public static int getDescIndex() {
        return sendDescCtr;
    }

    public static Map<Integer, HIDDevice> getAll() {
        return controllers;
    }
}