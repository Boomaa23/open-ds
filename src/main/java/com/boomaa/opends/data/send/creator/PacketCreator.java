package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.util.SequenceCounter;

public abstract class PacketCreator {
    protected static final SequenceCounter SEQUENCE_COUNTER = new SequenceCounter();

    public abstract byte[] dsToRioUdp(SendTag... tags);
    public abstract byte[] dsToRioTcp(SendTag... tags);
    public abstract byte[] dsToFmsUdp(SendTag... tags);
    public abstract byte[] dsToFmsTcp(SendTag... tags);

    public static PacketBuilder getSequenced() {
        return new PacketBuilder().addBytes(SEQUENCE_COUNTER.increment().getBytes());
    }
}
