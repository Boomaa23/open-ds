package com.boomaa.opends.usb;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.frames.JoystickFrame;

import java.util.LinkedList;
import java.util.List;

public class ControlDevices {
    private static List<HIDDevice> controllers = new LinkedList<>();
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
            for (HIDDevice hid : controllers) {
                if (hid.hasController(ctrl)) {
                    hasHid = true;
                    break;
                }
            }
            if (!hasHid) {
                HIDDevice hid = instantiateHID(ctrl);
                controllers.add(hid);
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
        for (HIDDevice hid : controllers) {
            if (hid.needsRemove()) {
                controllers.remove(hid);
                if (PopupBase.isVisible(JoystickFrame.class)) {
                    IndexTracker.unregister(hid.getIdx());
                    final int remIdx = Math.min(hid.getIdx(), JoystickFrame.EmbeddedJDEC.LIST_MODEL.size() - 1);
                    JoystickFrame.EmbeddedJDEC.LIST_MODEL.remove(remIdx);
                }
            }
        }
    }

    public static synchronized void clearAll() {
        NativeUSBManager.getOSInstance().clearDevices();
        IndexTracker.reset();
        controllers.clear();
    }

    public static synchronized void updateValues() {
        for (HIDDevice hid : controllers) {
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

    public static List<HIDDevice> getAll() {
        return controllers;
    }
}