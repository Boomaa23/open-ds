package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.util.Objects;

public abstract class HIDDevice {
    public static final int MAX_JS_NUM = 6; //max 6 joysticks
    protected boolean[] buttons;
    protected int hwIdx;
    protected int swIdx;
    protected final String name;
    protected boolean disabled;
    protected boolean queueRemove;

    public HIDDevice(int index, int numButtons, String name) {
        this.buttons = new boolean[numButtons];
        this.hwIdx = index;
        this.swIdx = index;
        this.name = name;
    }

    public abstract void update();

    public void updateButtons(ByteBuffer btns) {
        for (int i = 0; i < btns.limit(); i++) {
            setButton(i, btns.get(i) == GLFW.GLFW_PRESS);
        }
    }

    public void setButton(int index, boolean value) {
        this.buttons[index] = value;
    }

    public boolean getButton(int index) {
        return this.buttons[index];
    }

    public boolean[] getButtons() {
        return buttons;
    }

    public void setIndex(int index) {
        this.swIdx = index;
    }

    public int getIndex() {
        return swIdx;
    }

    public int getHardwareIndex() {
        return hwIdx;
    }

    public int numButtons() {
        return buttons.length;
    }

    public abstract int numAxes();

    public String getName() {
        return name;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void remove() {
        this.queueRemove = true;
    }

    public boolean needsRemove() {
        return queueRemove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HIDDevice hidDevice = (HIDDevice) o;
        return hwIdx == hidDevice.hwIdx &&
                name.equals(hidDevice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hwIdx, name);
    }

    @Override
    public String toString() {
        return name;
    }

    public interface Axis {
        int ordinal();

        default int getInt() {
            return this.ordinal();
        }
    }
}
