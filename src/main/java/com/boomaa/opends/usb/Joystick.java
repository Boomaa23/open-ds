package com.boomaa.opends.usb;

import net.java.games.input.Controller;

public class Joystick {
    protected final Controller controller;
    protected final int numButtons;
    protected double x = 0.0;
    protected double y = 0.0;
    protected double z = 0.0;
    protected boolean[] buttons;

    public Joystick(Controller controller, int numButtons) {
        this.controller = controller;
        this.buttons = new boolean[numButtons];
        this.numButtons = numButtons;
    }

    public Controller getController() {
        return controller;
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

    public void setButton(int index, boolean value) {
        this.buttons[index] = value;
    }

    public boolean getButton(int index) {
        return this.buttons[index];
    }

    public int numButtons() {
        return numButtons;
    }

    @Override
    public String toString() {
        return controller.getName();
    }
}
