package com.boomaa.opends.data.send.creator;

public abstract class NoTCPCreator extends PacketCreator {
    @Override
    public final byte[] dsToRioTcp() {
        // No TCP connections in LibDS
        return new byte[0];
    }

    @Override
    public final byte[] dsToFmsTcp() {
        // No TCP connections in LibDS
        return new byte[0];
    }
}
