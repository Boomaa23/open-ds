package com.boomaa.opends.usb;

import net.java.games.input.Component;
import net.java.games.input.Controller;

public abstract class HIDDevice {
    public static final int MAX_JS_NUM = 6; //max 6 joysticks
    protected final Controller controller;
    protected final int numButtons;
    protected boolean[] buttons;

    public HIDDevice(Controller controller) {
        this.controller = controller;
        this.numButtons = countButtons(controller);
        this.buttons = new boolean[numButtons];
    }

    public Controller getController() {
        return controller;
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

    public int numButtons() {
        return numButtons;
    }

    public abstract int numAxes();

    @Override
    public String toString() {
        return controller.getName();
    }

    private static int countButtons(Controller controller) {
        int btnCtr = 0;
        for (Component comp : controller.getComponents()) {
            if (comp.getIdentifier() instanceof Component.Identifier.Button) {
                btnCtr++;
            }
        }
        return btnCtr;
    }
}
