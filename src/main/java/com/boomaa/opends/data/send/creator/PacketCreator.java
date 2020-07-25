package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.util.SequenceCounter;

public abstract class PacketCreator {
    protected static final SequenceCounter SEQUENCE_COUNTER = new SequenceCounter();

    public abstract byte[] dsToRioUdp(SendTag tag);
    public abstract byte[] dsToRioTcp(SendTag tag);
    public abstract byte[] dsToFmsUdp(SendTag tag);
    public abstract byte[] dsToFmsTcp(SendTag tag);

    public byte[] dsToRioUdp() {
        return dsToRioUdp(null);
    }

    public byte[] dsToRioTcp() {
        return dsToRioTcp(null);
    }

    public byte[] dsToFmsUdp() {
        return dsToFmsUdp(null);
    }

    public byte[] dsToFmsTcp() {
        return dsToFmsTcp(null);
    }

    public static PacketBuilder getSequenced() {
        return new PacketBuilder().addBytes(SEQUENCE_COUNTER.increment().getBytes());
    }
}
