package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class USBInterface {
    private static List<HIDDevice> controlDevices = new ArrayList<>();

    static {
        GLFW.glfwInit();
        findControllers();
        updateValues();
    }

    public static void findControllers() {
        GLFW.glfwPollEvents();
        for (int idx = 0; idx < GLFW.GLFW_JOYSTICK_LAST; idx++) {
            if (GLFW.glfwJoystickPresent(idx)) {
                HIDDevice dev = GLFW.glfwJoystickIsGamepad(idx) ? new XboxController(idx) : new Joystick(idx);
                if (idx >= controlDevices.size()) {
                    controlDevices.add(idx, dev);
                } else {
                    controlDevices.set(idx, dev);
                }
            }
        }
    }

    public static void updateValues() {
        GLFW.glfwPollEvents();
        for (HIDDevice ctrl : controlDevices) {
            ctrl.update();
        }
    }

    public static List<HIDDevice> getControlDevices() {
        return controlDevices;
    }
}