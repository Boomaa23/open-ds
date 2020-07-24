package com.boomaa.opends.display;

import com.boomaa.opends.data.PacketCreator;
import com.boomaa.opends.data.PacketParser;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.networking.UDPInterface;
import com.boomaa.opends.util.Clock;
import com.boomaa.opends.util.NumberUtils;

public class DisplayEndpoint implements JDEC {
    private static UDPInterface.Send udpSendServer;
    private static UDPInterface.Receive udpReceieveServer;
    private static boolean hasInitialized = false;
    private static final Clock twentyMsClock = new Clock(20) {
        @Override
        public void onCycle() {
            if (hasInitialized) {
                updateDisplayElements(new PacketParser.RioToDsUdp(udpReceieveServer.doReceieve().getBuffer()));

                udpSendServer.doSend(PacketCreator.dsToRio());
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
            udpSendServer = new UDPInterface.Send("roboRIO-" + Integer.parseInt(JDEC.TEAM_NUMBER.getText()) + "-FRC.local", 1110);
            udpReceieveServer = new UDPInterface.Receive(1150);
            hasInitialized = true;
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static void updateDisplayElements(PacketParser.Common data) {
        if (data.getProtocol() == Protocol.UDP) {
            if (data.getRemote() == Remote.ROBO_RIO) {
                PacketParser.RioToDsUdp rioUdp = (PacketParser.RioToDsUdp) data;
                JDEC.HAS_BROWNOUT.setDisplay(rioUdp.getStatus().contains(Status.ESTOP));
                JDEC.CODE_INITIALIZING.setDisplay(rioUdp.getStatus().contains(Status.CODE_INIT));
                JDEC.ROBOT_CODE.setDisplay(rioUdp.getTrace().contains(Trace.ROBOTCODE));
                JDEC.BAT_VOLTAGE.setText(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2) + " V");
            } else if (data.getRemote() == Remote.FMS) {

            }
        } else if (data.getProtocol() == Protocol.TCP) {
            if (data.getRemote() == Remote.ROBO_RIO) {

            } else if (data.getRemote() == Remote.FMS) {

            }
        }
    }
}
