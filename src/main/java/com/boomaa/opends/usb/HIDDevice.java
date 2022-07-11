package com.boomaa.opends.usb;

import com.boomaa.opends.util.OperatingSystem;

import java.util.List;
import java.util.Objects;

public class HIDDevice {
    public static final int DEFAULT_AXIS_MAX = 6;
    private final Controller<?> ctrl;
    private final ComponentTracker axesTracker;
    private final ComponentTracker buttonTracker;
    protected int idx;
    protected boolean disabled;

    public HIDDevice(Controller<?> ctrl) {
        this.ctrl = ctrl;
        this.idx = IndexTracker.registerNext();
        ctrl.poll();

        this.axesTracker = ComponentTracker.fromFile(getAxesTrackerFilePath());
        this.buttonTracker = new ComponentTracker();
        List<? extends Component> comps = ctrl.getComponents();
        for (int i = 0; i < comps.size(); i++) {
            Component comp = comps.get(i);
            Component.Identifier id = comp.getIdentitifer();
            if (comp.isAxis()) {
                axesTracker.track(id, i);
            } else if (comp.isButton()) {
                buttonTracker.track(id, i);
            }
        }

        axesTracker.map(Component.Axis.X, Component.Axis.X, false)
                .map(Component.Axis.Y, Component.Axis.Y, false)
                .map(Component.Axis.Z, Component.Axis.RZ, false)
                .map(Component.Axis.RX, Component.Axis.RX, false)
                .map(Component.Axis.RY, Component.Axis.RY, false)
                .map(Component.Axis.RZ, Component.Axis.Z, false);
        buttonTracker.mapAllSelf(Component.Button.values());
    }

    public void update() {
        ctrl.poll();
    }

    public Component getComponent(int compIdx) {
        try {
            return ctrl.getComponents().get(compIdx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Component getComponent(Component.Identifier id) {
        return getComponent(id instanceof Component.Axis ? axesTracker.getIndex(id) : buttonTracker.getIndex(id));
    }

    public ComponentTracker getButtonTracker() {
        return buttonTracker;
    }

    public boolean[] getButtons() {
        List<? extends Component> comps = ctrl.getComponents();
        boolean[] buttons = new boolean[usedNumButtons()];
        int btnIdx = 0;
        for (Integer compIdx : buttonTracker.getDirectMap().values()) {
            buttons[btnIdx++] = comps.get(compIdx).getValue() == 1;
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

    public int usedNumButtons() {
        return buttonTracker.numMapped();
    }

    public int deviceNumButtons() {
        return ctrl.getNumButtons();
    }

    public ComponentTracker getAxesTracker() {
        return axesTracker;
    }

    public String getAxesTrackerFilePath() {
        return OperatingSystem.getTempFolder()
                + "ods-axestracker-"
                + getName().replaceAll("[^a-zA-Z0-9]", "")
                + ".conf";
    }

    public double getAxis(Component.Identifier id) {
        Component comp = getComponent(id);
        return comp == null ? Integer.MAX_VALUE : comp.getValue();
    }

    public int usedNumAxes() {
        return axesTracker.numMapped();
    }

    public final int deviceNumAxes() {
        return ctrl.getNumAxes();
    }

    public boolean hasController(Controller<?> ctrl) {
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
