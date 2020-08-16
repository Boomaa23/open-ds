package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.PopupBase;

public class RobotFrame extends PopupBase {
    @Override
    public void dispose() {
//        DisplayEndpoint.SIM_ROBOT.close();
        MainJDEC.SIMULATE_ROBOT.setSelected(false);
        MainJDEC.IS_ENABLED.setEnabled(MainJDEC.ROBOT_CONNECTION_STATUS.isDisplayed());
        super.dispose();
    }
}
