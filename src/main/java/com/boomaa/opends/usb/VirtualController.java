package com.boomaa.opends.usb;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A virtual controller that simulates a gamepad/joystick without any
 * real USB hardware. Axes and buttons can be set programmatically
 * from keyboard or on-screen button inputs.
 *
 * Layout:
 *   Axes: X (left stick X), Y (left stick Y), Z (right stick X),
 *          RX (right trigger), RY (right stick Y), RZ (left trigger)
 *   Buttons: 0-9 (A, B, X, Y, LB, RB, Back, Start, L3, R3)
 */
public class VirtualController extends Controller<VirtualComponent> {
    private static final int NUM_BUTTONS = 10;
    private static final int NUM_AXES = 6;
    private final Map<Component.Identifier, VirtualComponent> lookup = new LinkedHashMap<>();

    public VirtualController() {
        this.numAxes = NUM_AXES;
        this.numButtons = NUM_BUTTONS;
        initComponents();
    }

    private void initComponents() {
        // Axes
        addAxis(Component.Axis.X);
        addAxis(Component.Axis.Y);
        addAxis(Component.Axis.Z);
        addAxis(Component.Axis.RX);
        addAxis(Component.Axis.RY);
        addAxis(Component.Axis.RZ);

        // Buttons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            Component.Button id = Component.Button.values()[i];
            VirtualComponent btn = new VirtualComponent(id, false);
            objects.add(btn);
            lookup.put(id, btn);
        }
    }

    private void addAxis(Component.Axis axis) {
        VirtualComponent comp = new VirtualComponent(axis, true);
        objects.add(comp);
        lookup.put(axis, comp);
    }

    public void setAxis(Component.Axis axis, double value) {
        VirtualComponent comp = lookup.get(axis);
        if (comp != null) {
            comp.setValue(value);
        }
    }

    public void setButton(int index, boolean pressed) {
        if (index >= 0 && index < NUM_BUTTONS) {
            Component.Button id = Component.Button.values()[index];
            VirtualComponent comp = lookup.get(id);
            if (comp != null) {
                comp.setValue(pressed ? 1.0 : 0.0);
            }
        }
    }

    @Override
    public Type getType() {
        return Type.HID_GAMEPAD;
    }

    @Override
    public void poll() {
        // No-op: values are set directly from UI/keyboard
    }

    @Override
    public String getName() {
        return "Virtual Gamepad";
    }
}
