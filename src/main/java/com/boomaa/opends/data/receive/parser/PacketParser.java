package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.util.ArrayUtils;

public abstract class PacketParser {
    protected final byte[] packet;
    private final Protocol protocol;
    private final Remote remote;
    private final int tagStartIndex;
    private TagValueMap<?> tagValue;

    public PacketParser(byte[] packet, Protocol protocol, Remote remote, int tagStartIndex) {
        this.packet = packet;
        this.protocol = protocol;
        this.remote = remote;
        this.tagStartIndex = tagStartIndex + 1;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Remote getRemote() {
        return remote;
    }

    public abstract int getTagSize();

    public int getTagFlag() {
        if (tagStartIndex < packet.length) {
            return packet[tagStartIndex];
        }
        return -1;
    }

    public TagValueMap<?> getTag() {
        if (tagValue != null) {
            return tagValue;
        }
        if (tagStartIndex < packet.length) {
            for (ReceiveTag tag : ReceiveTag.values()) {
                if (tag.getRemote() == remote && tag.getProtocol() == protocol
                        && tag.getFlag() == packet[tagStartIndex]) {
                    TagValueMap<?> value = tag.getAction().getValue(ArrayUtils.sliceArr(packet, tagStartIndex), getTagSize());
                    this.tagValue = value;
                    return value;
                }
            }
        }
        return null;
    }

    public byte[] getPacket() {
        return packet;
    }
}
