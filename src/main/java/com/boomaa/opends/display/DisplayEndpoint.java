package com.boomaa.opends.display;

import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.display.updater.ElementUpdater;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.TCPServer;
import com.boomaa.opends.networking.UDPInterface;
import com.boomaa.opends.util.Clock;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DisplayEndpoint implements JDEC {
    private static UDPInterface rioUdpInterface;
    private static TCPInterface rioTcpInterface;
    private static UDPInterface fmsUdpInterface;
    private static TCPInterface fmsTcpInterface;
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
        //TODO do TCP check for mDNS address
        try {
            String rioIp = "roboRIO-" + Integer.parseInt(TEAM_NUMBER.getText()) + "-FRC.local";
            InetAddress.getByName(rioIp);

            rioUdpInterface = new UDPInterface(rioIp, 1150, 1110);
            rioTcpInterface = new TCPInterface(rioIp, 1740);

            boolean connFms = CONNECT_REAL_FMS.isSelected();
            if (!connFms) {
                TCPServer tcp = new TCPServer(1750) {
                    @Override
                    public void onRun(Socket client, OutputStream out, byte[] data) {
                        //TODO implement FMS simulation
                    }
                };
                tcp.start();
                fmsUdpInterface = new UDPInterface("localhost", 1160, 1160);
                fmsTcpInterface = new TCPInterface("localhost", 1750);
                tcp.close();
            } else {
                fmsUdpInterface = new UDPInterface("10.0.100.5", 1160, 1121);
                fmsTcpInterface = new TCPInterface("10.0.100.5", 1750);
            }
            hasInitialized = true;
            return true;
        } catch (NumberFormatException | UnknownHostException ignored) {
            return false;
        }
    }
    public static Integer[] getValidProtocolYears() {
        return new Integer[] {
                2020
        };
    }
}
