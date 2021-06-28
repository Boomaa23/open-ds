package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

import java.util.List;

public class Parser2014 {
    public static class RioToDsUdp extends NoTagParser {
        public RioToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Remote.ROBO_RIO);
        }

        public double getBatteryVoltage() {
            return packet[1] * 12.0 / 0x12 + (packet[2] * 12.0 / 0x12 / 0xFF);
        }

        public boolean isEmergencyStopped() {
            return packet[0] == Control.ESTOP.getFlag();
        }
    }

    // No TCP connections in LibDS
    public static class RioToDsTcp extends NoTagParser {
        public RioToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Remote.ROBO_RIO);
        }
    }

    public static class FmsToDsUdp extends NoTagParser {
        public FmsToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Remote.FMS);
        }

        public List<Control> getControl() {
            return super.getFlagDataAt(Control.values(), 2);
        }

        public AllianceStation getAllianceStation() {
            return new AllianceStation(packet[4] - 0x31, packet[3] != 0x52);
        }
    }

    // No TCP connections in LibDS
    public static class FmsToDsTcp extends NoTagParser {
        public FmsToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Remote.FMS);
        }
    }
}
