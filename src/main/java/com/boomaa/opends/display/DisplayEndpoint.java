package com.boomaa.opends.display;

import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.updater.ElementUpdater;
import com.boomaa.opends.networking.SimulatedFMS;
import com.boomaa.opends.networking.SimulatedRobot;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.UDPInterface;
import com.boomaa.opends.util.Clock;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DisplayEndpoint implements MainJDEC {
    private static UDPInterface rioUdpInterface;
    private static TCPInterface rioTcpInterface;
    private static UDPInterface fmsUdpInterface;
    private static TCPInterface fmsTcpInterface;
    private static SimulatedRobot simRobot = new SimulatedRobot();
    private static SimulatedFMS simFms = new SimulatedFMS();
    private static String parserClass = "com.boomaa.opends.data.receive.parser.Parser" + PROTOCOL_YEAR.getSelectedItem() + ".";
    private static String creatorClass = "com.boomaa.opends.data.send.creator.Creator" + PROTOCOL_YEAR.getSelectedItem();
    private static String updaterClass = "com.boomaa.opends.display.updater.Updater" + PROTOCOL_YEAR.getSelectedItem();
    private static boolean hasInitialized = false;
    private static final Clock twentyMsClock = new Clock(20) {
        @Override
        public void onCycle() {
            if (hasInitialized) {
                try {
                    ((ElementUpdater) Class.forName(updaterClass).getConstructor().newInstance()).updateFromRioUdp(
                            (PacketParser) Class.forName(parserClass + "RioToDsUdp").getConstructor(byte[].class).newInstance(rioUdpInterface.doReceieve().getBuffer()));
                    rioUdpInterface.doSend(((PacketCreator) Class.forName(creatorClass).getConstructor().newInstance()).dsToRioUdp());
                } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static void main(String[] args) {
        MainFrame.display();
        twentyMsClock.start();
    }

    public static boolean initServers() {
        try {
            if (!SIMULATE_ROBOT.isSelected()) {
                String rioIp = "roboRIO-" + Integer.parseInt(TEAM_NUMBER.getText()) + "-FRC.local";
                InetAddress.getByName(rioIp);
                if (simRobot.isAlive()) {
                    simRobot.close();
                }
                rioUdpInterface = new UDPInterface(rioIp, 1150, 1110);
                rioTcpInterface = new TCPInterface(rioIp, 1740);
            } else {
                if (!simRobot.isAlive()) {
                    simRobot.start();
                }
                rioUdpInterface = new UDPInterface("localhost", 1150, 1110);
                rioTcpInterface = new TCPInterface("localhost", 1740);
            }
            if (FMS_TYPE.getSelectedItem() == FMSType.REAL) {
                if (simFms.isAlive()) {
                    simFms.close();
                }
                fmsUdpInterface = new UDPInterface("10.0.100.5", 1160, 1121);
                fmsTcpInterface = new TCPInterface("10.0.100.5", 1750);
            } else if (FMS_TYPE.getSelectedItem() == FMSType.SIMULATED) {
                if (!simFms.isAlive()) {
                    simFms.start();
                }
                fmsUdpInterface = new UDPInterface("localhost", 1160, 1160);
                fmsTcpInterface = new TCPInterface("localhost", 1750);
            }
            hasInitialized = true;
        } catch (NumberFormatException | UnknownHostException ignored) {
        }
        return hasInitialized;
    }
    public static Integer[] getValidProtocolYears() {
        return new Integer[] {
                2020
        };
    }
}
