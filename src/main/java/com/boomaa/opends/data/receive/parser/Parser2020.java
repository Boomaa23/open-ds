package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.*;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser2020 {
    public static class RioToDsUdp extends PacketParser {
        private List<Status> statusListGlobal;
        private List<Trace> traceListGlobal;

        public RioToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Remote.ROBO_RIO, 8);
        }

        public int getSequenceNum() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }

        public int getCommVersion() {
            return packet[2]; //always 0x01
        }

        public List<Status> getStatus() {
            if (statusListGlobal != null) {
                return statusListGlobal;
            }
            List<Status> statusList = new ArrayList<>();
            for (Status status : Status.values()) {
                if (NumberUtils.hasMaskMatch(packet[3], status.getFlag(), status.getBitmaskPos())) {
                    statusList.add(status);
                }
            }
            this.statusListGlobal = statusList;
            return statusList;
        }

        public List<Trace> getTrace() {
            if (traceListGlobal != null) {
                return traceListGlobal;
            }
            List<Trace> traceList = new ArrayList<>();
            for (Trace trace : Trace.values()) {
                if (NumberUtils.hasMaskMatch(packet[4], trace.getFlag(), trace.getBitmaskPos())) {
                    traceList.add(trace);
                }
            }
            this.traceListGlobal = traceList;
            return traceList;
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

    public static class RioToDsTcp extends PacketParser {
        public RioToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Remote.ROBO_RIO, 0);
        }

        @Override
        public int getTagSize(int index) {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, index, index + 2));
        }
    }

    public static class FmsToDsUdp extends PacketParser {
        private List<Control> controlListGlobal;

        public FmsToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Remote.FMS, 23);
        }

        public int getSequenceNum() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }

        public int getCommVersion() {
            return packet[2]; //always 0x01
        }

        public List<Control> getControl() {
            if (controlListGlobal != null) {
                return controlListGlobal;
            }
            List<Control> controlList = new ArrayList<>();
            for (Control control : Control.values()) {
                if (NumberUtils.hasMaskMatch(packet[4], control.getFlag(), control.getBitmaskPos())) {
                    controlList.add(control);
                }
            }
            this.controlListGlobal = controlList;
            return controlList;
        }

        public int getRequest() {
            return packet[4]; // always 0x00, unused
        }

        public AllianceStation getAllianceStation() {
            return AllianceStation.getFromByte(packet[5]);
        }

        public String getTournamentLevel() {
            switch (packet[6]) {
                case 0: return "Match Test";
                case 1: return "Practice";
                case 2: return "Qualification";
                case 3: return "Playoff";
            }
            return null;
        }

        public int getMatchNumber() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 7, 9));
        }

        public int getPlayNumber() {
            return packet[9];
        }

        public Date getDate() {
            return Date.fromRecvBytes(ArrayUtils.sliceArr(packet, 10, 21));
        }

        public int getRemainingTime() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 21, 23));
        }

        @Override
        public int getTagSize(int index) {
            return packet[index];
        }
    }

    public static class FmsToDsTcp extends PacketParser {
        public FmsToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Remote.FMS, 0);
        }

        @Override
        public int getTagSize(int index) {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, index, index + 2));
        }
    }
}
