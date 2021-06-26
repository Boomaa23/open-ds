package com.boomaa.opends.usb;

import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.frames.JoystickFrame;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWJoystickCallback;

import java.util.HashMap;
import java.util.Map;

public class ControlDevices {
    private static Map<Integer, HIDDevice> controllers = new HashMap<>();
    private static int sendDataCtr = 0;
    private static int sendDescCtr = 0;

    public static void init() {
        GLFW.glfwInitHint(GLFW.GLFW_JOYSTICK_HAT_BUTTONS, GLFW.GLFW_FALSE);
        GLFW.glfwInit();
        findAll();
        GLFW.glfwSetJoystickCallback(GLFWJoystickCallback.create((idx, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                HIDDevice device = add(idx);
                if (PopupBase.isVisible("JoystickFrame")) {
                    final int listSize = JoystickFrame.EmbeddedJDEC.LIST_MODEL.size();
                    JoystickFrame.EmbeddedJDEC.LIST_MODEL.add(listSize, device);
                }
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                HIDDevice device = controllers.get(idx);
                if (PopupBase.isVisible("JoystickFrame")) {
                    final int remIdx = Math.min(device.getFRCIdx(), JoystickFrame.EmbeddedJDEC.LIST_MODEL.size() - 1);
                    JoystickFrame.EmbeddedJDEC.LIST_MODEL.remove(remIdx);
                }
                device.remove();
            }
        }));
        updateValues();
    }

    public static synchronized void findAll() {
        GLFW.glfwPollEvents();
        for (int idx = 0; idx < GLFW.GLFW_JOYSTICK_LAST; idx++) {
            if (GLFW.glfwJoystickPresent(idx)) {
                add(idx);
            }
        }
    }

    private static synchronized HIDDevice add(int idx) {
        HIDDevice device = GLFW.glfwJoystickIsGamepad(idx) ? new XboxController(idx) : new Joystick(idx);
        controllers.put(idx, device);
        return device;
    }

    public static synchronized void clearAll() {
        controllers.clear();
    }

    public static synchronized void updateValues() {
        GLFW.glfwPollEvents();
        for (int i = 0; i <= HIDDevice.MAX_JS_INDEX; i++) {
            HIDDevice ctrl = controllers.get(i);
            if (ctrl != null) {
                if (ctrl.needsRemove()) {
                    controllers.remove(ctrl.getFRCIdx());
                } else {
                    ctrl.update();
                }
            }
        }
    }

    public static synchronized void reindexAll() {
        Map<Integer, HIDDevice> deviceMapTemp = new HashMap<>(controllers);
        clearAll();
        for (HIDDevice device : deviceMapTemp.values()) {
            int devIdx = device.getFRCIdx();
            controllers.put(devIdx, device);
        }
    }

    public synchronized static int iterateSend(boolean isData) {
        int out;
        if (isData) {
            out = sendDataCtr++;
            sendDataCtr %= (HIDDevice.MAX_JS_INDEX + 1);
        } else {
            out = sendDescCtr++;
            sendDescCtr %= HIDDevice.MAX_JS_NUM;
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