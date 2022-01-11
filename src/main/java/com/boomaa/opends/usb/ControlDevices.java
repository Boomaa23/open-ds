package com.boomaa.opends.usb;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.frames.JoystickFrame;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControlDevices {
    private static final Map<Integer, HIDDevice> controllers = new LinkedHashMap<>();
    private static int sendDataCtr = 0;
    private static int sendDescCtr = 0;

    public static void init() {
        findAll();
        updateValues();
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
                HIDDevice hid = instantiateHID(ctrl);
                controllers.put(hid.getIdx(), hid);
                if (PopupBase.isVisible(JoystickFrame.class)) {
                    final int addIdx = Math.min(hid.getIdx(), JoystickFrame.EmbeddedJDEC.LIST_MODEL.size());
                    JoystickFrame.EmbeddedJDEC.LIST_MODEL.add(addIdx, hid);
                }
            }
        }
    }

    private static synchronized HIDDevice instantiateHID(Controller<?> ctrl) {
        switch (ctrl.getType()) {
            case HID_GAMEPAD:
                return new XboxController(ctrl);
            case HID_JOYSTICK:
            default:
                return new Joystick(ctrl);
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
                if (PopupBase.isAlive(JoystickFrame.class)) {
                    IndexTracker.unregister(hid.getIdx());
                    final int remIdx = Math.min(hid.getIdx(), JoystickFrame.EmbeddedJDEC.LIST_MODEL.size() - 1);
                    JoystickFrame.EmbeddedJDEC.LIST_MODEL.remove(remIdx);
                }
            }
        }
    }

    public static synchronized void reindexAll() {
        Map<Integer, HIDDevice> deviceMapTemp = new HashMap<>(controllers);
        controllers.clear();
        for (HIDDevice device : deviceMapTemp.values()) {
            int devIdx = device.getIdx();
            controllers.put(devIdx, device);
        }
    }

    public static synchronized void clearAll() {
        NativeUSBManager.getOSInstance().clearDevices();
        IndexTracker.reset();
        controllers.clear();
    }

    public static synchronized void updateValues() {
        for (HIDDevice hid : controllers.values()) {
            hid.update();
        }
    }

    public synchronized static int iterateSend(boolean isData) {
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