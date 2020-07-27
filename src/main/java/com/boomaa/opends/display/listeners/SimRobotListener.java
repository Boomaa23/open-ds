package com.boomaa.opends.display.listeners;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.frames.RobotFrame;
import com.boomaa.opends.networking.SimulatedRobot;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.UDPInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SimRobotListener extends DisplayEndpoint implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        reload();
    }

    public static void reload() {
        try {
            IS_ENABLED.setEnabled(SIMULATE_ROBOT.isSelected());
            if (SIMULATE_ROBOT.isSelected()) {
                if (RIO_UDP_INTERFACE != null && !RIO_UDP_INTERFACE.isClosed()) {
                    RIO_UDP_INTERFACE.close();
                }
                if (RIO_TCP_INTERFACE != null && !RIO_TCP_INTERFACE.isClosed()) {
                    RIO_TCP_INTERFACE.close();
                }
                SIM_ROBOT = new SimulatedRobot();
                ROBOT_FRAME = new RobotFrame();
            } else {
                if (SIM_ROBOT != null && !SIM_ROBOT.isClosed()) {
                    SIM_ROBOT.close();
                }
                if (ROBOT_FRAME != null) {
                    ROBOT_FRAME.dispose();
                }
//                String rioIp = "roboRIO-" + Integer.parseInt(TEAM_NUMBER.getText()) + "-FRC.local";
                String rioIp = "localhost"; //TODO remove after testing
                InetAddress.getByName(rioIp);
                RIO_UDP_INTERFACE = new UDPInterface(rioIp, 1110, 1150);
//                RIO_TCP_INTERFACE = new TCPInterface(rioIp, 1740);
            }
        } catch (UnknownHostException ignored) {
        }
    }
}
