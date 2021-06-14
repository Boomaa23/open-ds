package com.boomaa.opends.usb;

import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class HIDDevice {
    public static final int MAX_JS_NUM = 6; //max 6 joysticks
    public static int MAX_JS_INDEX = 0; //maximum populated index (var)
    protected boolean[] buttons;
    protected int glfwIdx;
    protected int frcIdx;
    protected String name;
    protected final JoystickType jsType;
    protected final Axes axes;
    protected boolean disabled;
    protected boolean queueRemove;

    public HIDDevice(int index, int numButtons, String name, JoystickType jsType) {
        this.buttons = new boolean[numButtons];
        this.glfwIdx = index;
        this.frcIdx = index;
        this.name = name;
        this.jsType = jsType;
        this.axes = provideAxes();
        checkMax();
    }

    public HIDDevice(int index, int numButtons, JoystickType jsType) {
        this(index, numButtons, GLFW.glfwGetJoystickName(index), jsType);
    }

    private void checkMax() {
        if (frcIdx > MAX_JS_INDEX) {
            MAX_JS_INDEX = frcIdx;
        }
    }

    protected abstract void doUpdate();

    public void update() {
        if (!queueRemove) {
            doUpdate();
        }
    }

    public void updateButtons(ByteBuffer glfwBtns) {
        if (glfwBtns != null) {
            for (int i = 0; i < glfwBtns.limit(); i++) {
                setButton(i, glfwBtns.get(i) == GLFW.GLFW_PRESS);
            }
        } else {
            remove();
        }
    }

    public void updateAxes(FloatBuffer glfwAxes) {
        if (glfwAxes != null) {
            //TODO arduino leonardo has 11 axes at -1, remove them?
            for (HIDDevice.Axis axis : getAxes().values()) {
                axis.setValue(glfwAxes.get(axis.getGLFWIdx()));
            }
        } else {
            remove();
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

    public void setFRCIdx(int index) {
        this.frcIdx = index;
        checkMax();
    }

    public int getFRCIdx() {
        return frcIdx;
    }

    public int getGLFWIdx() {
        return glfwIdx;
    }

    public int numButtons() {
        return buttons.length;
    }

    protected abstract Axes provideAxes();

    public Axes getAxes() {
        return axes;
    }

    public final int numAxes() {
        return axes.size();
    }

    public String getName() {
        return name;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public JoystickType getDeviceType() {
        return jsType;
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
        return glfwIdx == hidDevice.glfwIdx &&
                name.equals(hidDevice.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(glfwIdx, name);
    }

    @Override
    public String toString() {
        return name;
    }

    public static class Axes extends LinkedHashMap<String, Axis> {
        private Set<String> path;
        private boolean valuesChanged = true;

        @Override
        public Axis put(String key, Axis value) {
            valuesChanged = true;
            return super.put(key, value);
        }

        @Override
        public Axis remove(Object key) {
            valuesChanged = true;
            return super.remove(key);
        }

        public static Axes create(Axis... axisList) {
            Axes axes = new Axes();
            for (Axis axis : axisList) {
                axes.put(axis.getName(), axis);
            }
            return axes;
        }

        // Precalculates optimal path through FRC indices (for sending)
        public Set<String> calcIdxPath() {
            if (this.path == null || valuesChanged) {
                Map<String, Integer> path = new LinkedHashMap<>();
                for (Axis axis : values()) {
                    int idx = axis.getFRCIdx();
                    if (idx != -1) {
                        path.put(axis.getName(), idx);
                    }
                }
                List<Map.Entry<String, Integer>> list = new ArrayList<>(path.entrySet());
                list.sort(Map.Entry.comparingByValue());

                Map<String, Integer> result = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : list) {
                    result.put(entry.getKey(), entry.getValue());
                }
                this.path = result.keySet();
                valuesChanged = false;
            }
            return this.path;
        }
    }

    public static class Axis {
        private final String name;
        private final int glfwIdx;
        private final int frcIdx;
        private double value;

        // -1 FRC index means do not include
        public Axis(String name, int glfwIdx, int frcIdx) {
            this.name = name;
            this.glfwIdx = glfwIdx;
            this.frcIdx = frcIdx;
        }

        public String getName() {
            return name;
        }

        public int getGLFWIdx() {
            return glfwIdx;
        }

        public int getFRCIdx() {
            return frcIdx;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }
}
