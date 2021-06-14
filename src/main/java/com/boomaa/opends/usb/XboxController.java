package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

public class XboxController extends HIDDevice {
    public XboxController(int index) {
        //TODO why is this 15 when jsBtns GLFW call returns 14?
        super(index, 15, JoystickType.XINPUT_GAMEPAD);
    }

    @Override
    protected void doUpdate() {
        GLFWGamepadState state = GLFWGamepadState.create();
        if (GLFW.glfwGetGamepadState(glfwIdx, state)) {
            updateButtons(state.buttons());
            updateAxes(state.axes());
        } else {
            remove();
        }
    }

    @Override
    protected Axes provideAxes() {
        return Axes.create(
                new Axis("Left X", GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, 0),
                new Axis("Right X", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X, 4),
                new Axis("Left Y", GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, 1),
                new Axis("Right Y", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y, 5),
                new Axis("Left Trigger", GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, 2),
                new Axis("Right Trigger", GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, 3)
        );
    }
}
