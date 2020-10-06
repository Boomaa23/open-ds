package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class USBInterface {
    private static Map<Integer, HIDDevice> controlDevices = new HashMap<>();

    public static void init() {
        GLFW.glfwInit();
        findControllers();
        updateValues();
    }

    public static void findControllers() {
        GLFW.glfwPollEvents();
        for (int idx = 0; idx < GLFW.GLFW_JOYSTICK_LAST; idx++) {
            if (GLFW.glfwJoystickPresent(idx)) {
                controlDevices.put(idx, GLFW.glfwJoystickIsGamepad(idx) ? new XboxController(idx) : new Joystick(idx));
            }
        }
    }

    public static synchronized void updateValues() {
        GLFW.glfwPollEvents();
        int size = controlDevices.size();
        for (int i = 0; i < size; i++) {
            HIDDevice ctrl = controlDevices.get(i);
            if (ctrl != null) {
                if (ctrl.needsRemove()) {
                    controlDevices.remove(ctrl.getHardwareIndex());
                } else {
                    ctrl.update();
                }
            }
        }
    }

    public static Map<Integer, HIDDevice> getControlDevices() {
        return controlDevices;
    }
}