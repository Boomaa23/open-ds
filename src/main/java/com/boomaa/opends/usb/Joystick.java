package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Joystick extends HIDDevice {
    protected double x = 0.0;
    protected double y = 0.0;
    protected double z = 0.0;

    public Joystick(int index) {
        super(index, GLFW.glfwGetJoystickButtons(index).limit(), GLFW.glfwGetJoystickName(index));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public void update() {
        if (!queueRemove) {
            ByteBuffer btns = GLFW.glfwGetJoystickButtons(hwIdx);
            if (btns != null) {
                updateButtons(btns);
            } else {
                remove();
            }

            FloatBuffer axes = GLFW.glfwGetJoystickAxes(hwIdx);
            if (axes != null) {
                setX(axes.get(0));
                setY(axes.get(1));
                setZ(axes.get(2));
            } else {
                remove();
            }
        }
    }

    @Override
    public int numAxes() {
        return 3;
    }

    public enum Axis implements HIDDevice.Axis {
        X, Y, Z
    }
}
