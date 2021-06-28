package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

public class Joystick extends HIDDevice {
    public Joystick(int index) {
        super(index, GLFW.glfwGetJoystickButtons(index).limit(), JoystickType.HID_JOYSTICK);
    }

    @Override
    protected void doUpdate() {
        updateButtons(GLFW.glfwGetJoystickButtons(glfwIdx));
        updateAxes(GLFW.glfwGetJoystickAxes(glfwIdx));
    }

    @Override
    protected Axes provideAxes() {
        return Axes.create(
                new Axis("X", 0, 0),
                new Axis("Y", 1, 1),
                new Axis("Z", 2, 2)
        );
    }
}
