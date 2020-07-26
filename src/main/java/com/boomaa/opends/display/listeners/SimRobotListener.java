package com.boomaa.opends.display.listeners;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.RobotFrame;
import com.boomaa.opends.networking.SimulatedRobot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimRobotListener implements ActionListener, MainJDEC {
    private RobotFrame frame;
    private SimulatedRobot networking;

    @Override
    public void actionPerformed(ActionEvent e) {
        IS_ENABLED.setEnabled(SIMULATE_ROBOT.isSelected());
        if (SIMULATE_ROBOT.isSelected()) {
            frame = new RobotFrame();
            networking = new SimulatedRobot();
        } else if (frame != null) {
            frame.dispose();
            networking.close();
        }
    }
}
