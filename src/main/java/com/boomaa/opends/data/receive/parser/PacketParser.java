package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.DataBase;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.ReceiveTagAction;
import com.boomaa.opends.data.receive.RefRecieveTag;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;
import com.boomaa.opends.util.PacketCounters;
import com.boomaa.opends.util.SequenceCounter;

import java.util.ArrayList;
import java.util.List;

public abstract class PacketParser {
    protected final byte[] packet;
    protected final Remote remote;
    protected final Protocol protocol;

    protected final int tagStartIndex;
    protected final SequenceCounter packetCounter;
    protected final TVMList tagValues = new TVMList();

    public PacketParser(byte[] packet, Remote remote, Protocol protocol, int tagStartIndex) {
        this.packet = packet;
        this.remote = remote;
        this.protocol = protocol;
        this.tagStartIndex = tagStartIndex;
        this.packetCounter = PacketCounters.get(remote, protocol);
    }

    public Remote getRemote() {
        return remote;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public SequenceCounter getPacketCounter() {
        return packetCounter;
    }

    public abstract int getTagSize(int index);

    public TVMList getTags() {
        if (tagValues.size() != 0) {
            return tagValues;
        }
        if (tagStartIndex < packet.length) {
            byte[] tagPacket = ArrayUtils.slice(packet, tagStartIndex);
            int c = 0;
            int size;
            while (true) {
                size = getTagSize(c + tagStartIndex);
                if (c + 1 >= tagPacket.length || c + 1 < 0) {
                    break;
                }
                for (ReceiveTag tag : ReceiveTag.values()) {
                    if (tag.getRemote() == remote && tag.getProtocol() == protocol && tag.getFlag() == tagPacket[c + 1]) {
                        ReceiveTagAction<?> action = tag.getActions()[MainJDEC.getProtocolIndex()];
                        if (action instanceof RefRecieveTag) {
                            action = tag.getActions()[((RefRecieveTag) action).getIndex()];
                        }
                        this.tagValues.add(action.getValue(ArrayUtils.slice(tagPacket, c + 2, c + size + 1), size).setBaseTag(tag));
                    }
                }
                c += size + 1;
            }
        }
        return tagValues;
    }

    public <T extends DataBase.Holder> List<T> getFlagDataAt(T[] values, int index) {
        List<T> outList = new ArrayList<>();
        for (T data : values) {
            if (NumberUtils.hasMaskMatch(packet[index], data.getFlag())) {
                outList.add(data);
            }
        }
        return outList;
    }

    public byte[] getPacket() {
        return packet;
    }
}
