package com.boomaa.opends.usb;

import net.java.games.input.Controller;

public class XboxController extends HIDDevice {
    protected double leftX = 0.0;
    protected double leftY = 0.0;
    protected double rightX = 0.0;
    protected double rightY = 0.0;
    protected double leftTrigger = 0.0;
    protected double rightTrigger = 0.0;

    public XboxController(Controller controller) {
        super(controller);
    }

    public double getX(boolean isLeft) {
        return isLeft ? leftX : rightX;
    }

    public void setX(double x, boolean isLeft) {
        if (isLeft) {
            this.leftX = x;
        } else {
            this.rightX = x;
        }
    }

    public double getY(boolean isLeft) {
        return isLeft ? leftY : rightY;
    }

    public void setY(double y, boolean isLeft) {
        if (isLeft) {
            this.leftY = y;
        } else {
            this.rightY = y;
        }
    }

    public double getTrigger(boolean isLeft) {
        return isLeft ? leftTrigger : rightTrigger;
    }

    public void setTrigger(double trigger, boolean isLeft) {
        if (isLeft) {
            this.leftTrigger = trigger;
        } else {
            this.rightTrigger = trigger;
        }
    }

    @Override
    public int numAxes() {
        return 6;
    }

    public enum Axis implements HIDDevice.Axis {
        LEFT_X, LEFT_Y, LEFT_TRIGGER, RIGHT_TRIGGER, RIGHT_X, RIGHT_Y
    }
}
