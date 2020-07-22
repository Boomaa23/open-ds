package com.boomaa.opends.data;

import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.tags.Tag;
import com.boomaa.opends.data.tags.TagValueMap;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public class PacketParser {
    public static class RioToDsUdp extends Common {
        public RioToDsUdp(byte[] packet) {
            super(packet, Tag.Protocol.UDP, Tag.Source.ROBO_RIO, 8);
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
            super(packet, Tag.Protocol.TCP, Tag.Source.ROBO_RIO, 2);
        }

        @Override
        public int getTagSize() {
            return NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2));
        }
    }

    //TODO FMS to DS input parsing

    public abstract static class Common {
        protected final byte[] packet;
        private final Tag.Protocol protocol;
        private final Tag.Source source;
        private final int tagStartIndex;

        public Common(byte[] packet, Tag.Protocol protocol, Tag.Source source, int tagStartIndex) {
            this.packet = packet;
            this.protocol = protocol;
            this.source = source;
            this.tagStartIndex = tagStartIndex + 1;
        }

        public abstract int getTagSize();

        public TagValueMap<?> getTag() {
            for (Tag tag : Tag.values()) {
                if (tag.getSource() == source && tag.getProtocol() == protocol
                        && tag.getFlag() == packet[tagStartIndex]) {
                    return tag.getAction().getValue(ArrayUtils.sliceArr(packet, tagStartIndex), getTagSize());
                }
            }
            return null;
        }

        public byte[] getPacket() {
            return packet;
        }
    }
}
