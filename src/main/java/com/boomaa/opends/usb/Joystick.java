package com.boomaa.opends.usb;

import net.java.games.input.Controller;

public class Joystick extends HIDDevice {
    protected double x = 0.0;
    protected double y = 0.0;
    protected double z = 0.0;

    public Joystick(Controller controller, int index) {
        super(controller, index);
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
    public int numAxes() {
        return 3;
    }

    public enum Axis implements HIDDevice.Axis {
        X, Y, Z, TWIST, THROTTLE
    }
}
