package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Control;

public enum RobotMode {
    TELEOPERATED(Control.TELEOP_MODE),
    AUTONOMOUS(Control.AUTO_MODE),
    TEST(Control.TEST_MODE);

    private final Control assocControl;

    RobotMode(Control assocControl) {
        this.assocControl = assocControl;
    }

    @Override
    public String toString() {
        String uname = super.toString();
        return Character.toUpperCase(uname.charAt(0)) + uname.substring(1).toLowerCase();
    }

    public Control getControlFlag() {
        return assocControl;
    }
}
