package com.boomaa.opends.usb;

import java.util.Objects;

public abstract class HIDDevice {
    private final Controller ctrl;
    private int[] axesPath;
    private int[] buttonPath;
    protected int idx;
    protected boolean disabled;

    public HIDDevice(Controller ctrl) {
        this.ctrl = ctrl;
        this.idx = IndexTracker.registerNext();
        ctrl.poll();
        Component[] comps = ctrl.getComponents();
        Component.Identifier[] axes = provideAxes();
        Component.Identifier[] buttons = provideButtons();
        this.axesPath = new int[axes.length];
        boolean isAllBtns = buttons == null;
        this.buttonPath = new int[isAllBtns ? deviceNumButtons() : buttons.length];
        int aCtr = 0;
        int bCtr = 0;
        for (int idx = 0; idx < comps.length; idx++) {
            Component comp = comps[idx];
            if (comp.isAxis()) {
                for (Component.Identifier axisId : axes) {
                    if (comp.getIdentitifer() == axisId) {
                        axesPath[aCtr++] = idx;
                    }
                }
            } else if (comp.isButton()) {
                if (isAllBtns) {
                    buttonPath[bCtr++] = idx;
                } else {
                    for (Component.Identifier btnId : buttons) {
                        if (comp.getIdentitifer() == btnId) {
                            buttonPath[bCtr++] = idx;
                        }
                    }
                }
            }
        }
    }

    public void update() {
        ctrl.poll();
    }

    public boolean[] getButtons() {
        Component[] comps = ctrl.getComponents();
        boolean[] buttons = new boolean[usedNumButtons()];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = comps[buttonPath[i]].getValue() == 1;
        }
        return buttons;
    }

    public void setIdx(int index) {
        IndexTracker.unregister(idx);
        IndexTracker.register(index);
        this.idx = index;
    }

    public int getIdx() {
        return idx;
    }

    // Default value null = include all buttons
    public Component.Identifier[] provideButtons() {
        return null;
    }

    public int usedNumButtons() {
        return buttonPath.length;
    }

    public int deviceNumButtons() {
        return ctrl.getNumButtons();
    }

    public abstract Component.Identifier[] provideAxes();

    public double getAxis(int idx) {
        return ctrl.getComponents()[axesPath[idx]].getValue();
    }

    public int usedNumAxes() {
        return axesPath.length;
    }

    public final int deviceNumAxes() {
        return ctrl.getNumAxes();
    }

    public double getComponentValue(Component.Identifier id) {
        for (Component comp : ctrl.getComponents()) {
            if (comp.getIdentitifer() == id) {
                return comp.getValue();
            }
        }
        return Integer.MAX_VALUE;
    }

    public boolean hasController(Controller ctrl) {
        return ctrl.equals(this.ctrl);
    }

    public String getName() {
        return ctrl.getName();
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean needsRemove() {
        return ctrl.needsRemove();
    }

    public Controller.Type getDeviceType() {
        return ctrl.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HIDDevice hidDevice = (HIDDevice) o;
        return ctrl == hidDevice.ctrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ctrl);
    }

    @Override
    public String toString() {
        return getName();
    }
}
