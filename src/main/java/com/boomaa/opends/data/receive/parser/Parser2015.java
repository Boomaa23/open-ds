package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.holders.Status;

import java.util.List;

public class Parser2015 {
    public static class RioToDsUdp extends PacketParser {
        public RioToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Remote.ROBO_RIO, 8);
        }

        public List<Control> getControl() {
            return super.getFlagDataAt(Control.values(), 3);
        }

        public List<Status> getStatus() {
            return super.getFlagDataAt(Status.values(), 4);
        }

        public List<Request> getRequest() {
            return super.getFlagDataAt(Request.values(), 7);
        }

        public double getBatteryVoltage() {
            return (double) packet[5] + ((double) packet[6]) / 256;
        }

        public boolean isRequestingDate() {
            return packet[7] == 0x01;
        }

        @Override
        public int getTagSize(int index) {
            return packet[index];
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
            return super.getFlagDataAt(Control.values(), 3);
        }

        public AllianceStation getAllianceStation() {
            return AllianceStation.getFromByte(packet[5]);
        }
    }

    // No TCP connections in LibDS
    public static class FmsToDsTcp extends NoTagParser {
        public FmsToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Remote.FMS);
        }
    }
}
