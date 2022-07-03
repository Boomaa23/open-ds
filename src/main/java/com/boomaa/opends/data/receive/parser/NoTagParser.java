package com.boomaa.opends.data.receive.parser;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

public class NoTagParser extends PacketParser {
    public NoTagParser(byte[] packet, Remote remote, Protocol protocol) {
        super(packet, remote, protocol, 0);
    }

    @Override
    public final int getTagSize(int index) {
        return 0;
    }
}
