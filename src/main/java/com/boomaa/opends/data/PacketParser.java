package com.boomaa.opends.data;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.tags.Tag;
import com.boomaa.opends.data.tags.TagValueMap;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public class PacketParser {
    public static class RioToDsUdp extends Common {
        public RioToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Source.ROBO_RIO, 8);
        }

        public int getSequenceNum() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }

        public int getCommVersion() {
            return packet[2]; //always 0x01
        }

        public Status getStatus() {
            for (Status status : Status.values()) {
                if (status.getValue() == packet[3]) {
                    return status;
                }
            }
            return null;
        }

        public Trace getTrace() {
            for (Trace trace : Trace.values()) {
                if (trace.getValue() == packet[4]) {
                    return trace;
                }
            }
            return null;
        }

        public double getBatteryVoltage() {
            return (int) packet[5] + ((int) packet[6]) / 256.0;
        }

        public boolean isRequestingDate() {
            return packet[7] == 0x01;
        }

        @Override
        public int getTagSize() {
            return packet[8];
        }
    }

    public static class RioToDsTcp extends Common {
        public RioToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Source.ROBO_RIO, 2);
        }

        @Override
        public int getTagSize() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }
    }

    public static class FmsToDsUdp extends Common {
        public FmsToDsUdp(byte[] packet) {
            super(packet, Protocol.UDP, Source.FMS, 23);
        }

        public int getSequenceNum() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }

        public int getCommVersion() {
            return packet[2]; //always 0x01
        }

        public Control getControl() {
            for (Control control : Control.values()) {
                if (control.getValue() == packet[3]) {
                    return control;
                }
            }
            return null;
        }

        public int getRequest() {
            return packet[4]; // always 0x00, unused
        }

        public AllianceStation getAllianceStation() {
            return AllianceStation.getFromByte(packet[5]);
        }

        public String getTournamentLevel() {
            int val = NumberUtils.getUInt8(packet[6]);
            switch (val) {
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
            return NumberUtils.getUInt8(packet[9]);
        }

        public Date getDate() {
            return Date.fromBytes(ArrayUtils.sliceArr(packet, 10, 21));
        }

        public int getRemainingTime() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 21, 23));
        }

        @Override
        public int getTagSize() {
            return packet.length - 22;
        }
    }

    public static class FmsToDsTcp extends Common {
        public FmsToDsTcp(byte[] packet) {
            super(packet, Protocol.TCP, Source.FMS, 3);
        }

        @Override
        public int getTagSize() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }
    }

    public abstract static class Common {
        protected final byte[] packet;
        private final Protocol protocol;
        private final Source source;
        private final int tagStartIndex;

        public Common(byte[] packet, Protocol protocol, Source source, int tagStartIndex) {
            this.packet = packet;
            this.protocol = protocol;
            this.source = source;
            this.tagStartIndex = tagStartIndex + 1;
        }

        public abstract int getTagSize();

        public int getTagFlag() {
            if (tagStartIndex < packet.length) {
                return packet[tagStartIndex];
            }
            return -1;
        }

        public TagValueMap<?> getTag() {
            if (tagStartIndex < packet.length) {
                for (Tag tag : Tag.values()) {
                    if (tag.getSource() == source && tag.getProtocol() == protocol
                            && tag.getFlag() == packet[tagStartIndex]) {
                        return tag.getAction().getValue(ArrayUtils.sliceArr(packet, tagStartIndex), getTagSize());
                    }
                }
            }
            return null;
        }

        public byte[] getPacket() {
            return packet;
        }
    }
}
