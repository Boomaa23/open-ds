package com.boomaa.opends.networking;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.display.frames.RobotFrame;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkReloader extends DisplayEndpoint {
    public static void reloadRio() {
        try {
            MainJDEC.IS_ENABLED.setEnabled(MainJDEC.SIMULATE_ROBOT.isSelected());
            if (MainJDEC.SIMULATE_ROBOT.isSelected()) {
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
                String rioIp = "localhost"; //TODO remove after testing, change to AddressConstants.getRioAddress()
                InetAddress.getByName(rioIp);
                RIO_UDP_INTERFACE = new UDPInterface(rioIp, AddressConstants.RIO_PORTS.getUdpClient(),  AddressConstants.RIO_PORTS.getUdpServer());
                RIO_TCP_INTERFACE = new TCPInterface(rioIp, AddressConstants.RIO_PORTS.getTcp());
            }
        } catch (UnknownHostException ignored) {
        }
    }

    public static void reloadFms() {
        if (FMS_UDP_INTERFACE != null) {
            FMS_UDP_INTERFACE.close();
        }
        if (FMS_TCP_INTERFACE != null) {
            FMS_TCP_INTERFACE.close();
        }
        FMS_UDP_INTERFACE = null;
        FMS_TCP_INTERFACE = null;
        if (SIM_FMS != null) {
            SIM_FMS.close();
        }

        FMSType type = (FMSType) MainJDEC.FMS_TYPE.getSelectedItem();
        switch (type) {
            case SIMULATED:
                FMS_FRAME = new FMSFrame();
                SIM_FMS = new SimulatedFMS();
                break;
            case REAL:
                if (FMS_FRAME != null) {
                    FMS_FRAME.dispose();
                }
                FMS_UDP_INTERFACE = new UDPInterface(AddressConstants.FMS_IP, AddressConstants.FMS_PORTS.getUdpClient(), AddressConstants.FMS_PORTS.getUdpClient());
                FMS_TCP_INTERFACE = new TCPInterface(AddressConstants.FMS_IP, AddressConstants.FMS_PORTS.getTcp());
                break;
        }
    }
}
