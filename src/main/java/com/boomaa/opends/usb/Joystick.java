package com.boomaa.opends.usb;

import net.java.games.input.Controller;

public class Joystick {
    private final Controller controller;
    private double x = 0.0;
    private double y = 0.0;
    private double z = 0.0;
    private boolean[] buttons;

    public Joystick(Controller controller, int numButtons) {
        this.controller = controller;
        this.buttons = new boolean[numButtons];
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
}
