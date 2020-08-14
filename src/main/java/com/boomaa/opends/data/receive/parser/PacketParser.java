package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.util.ArrayUtils;

public abstract class PacketParser {
    protected final byte[] packet;
    private final Protocol protocol;
    private final Remote remote;
    private final int tagStartIndex;
    private TVMList tagValues = new TVMList();

    public PacketParser(byte[] packet, Protocol protocol, Remote remote, int tagStartIndex) {
        this.packet = packet;
        this.protocol = protocol;
        this.remote = remote;
        this.tagStartIndex = tagStartIndex;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Remote getRemote() {
        return remote;
    }

    public abstract int getTagSize(int index);

    public TVMList getTags() {
        if (tagValues.size() != 0) {
            return tagValues;
        }
        if (tagStartIndex < packet.length) {
            byte[] tagPacket = ArrayUtils.sliceArr(packet, tagStartIndex);
            int c = 0;
            while (c < tagPacket.length) {
                int size = getTagSize(c + tagStartIndex);
                for (ReceiveTag tag : ReceiveTag.values()) {
                    if (tag.getRemote() == remote && tag.getProtocol() == protocol && tag.getFlag() == tagPacket[c + 1]) {
                        this.tagValues.add(tag.getAction().getValue(ArrayUtils.sliceArr(tagPacket, c + 2, c + size + 1), size).setBaseTag(tag));
                    }
                }
                c += size + 1;
            }
        }
        return tagValues;
    }

    public byte[] getPacket() {
        return packet;
    }
}
