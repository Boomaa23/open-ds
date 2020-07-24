package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.data.send.creator.Creator2020;
import com.boomaa.opends.display.frames.MainFrame;
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
                updateDisplayElements(new Parser2020.RioToDsUdp(udpReceieveServer.doReceieve().getBuffer()));
                udpSendServer.doSend(new Creator2020().dsToRioUdp());
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

    public static void updateDisplayElements(PacketParser data) {
        if (data.getProtocol() == Protocol.UDP) {
            if (data.getRemote() == Remote.ROBO_RIO) {
                Parser2020.RioToDsUdp rioUdp = (Parser2020.RioToDsUdp) data;
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
