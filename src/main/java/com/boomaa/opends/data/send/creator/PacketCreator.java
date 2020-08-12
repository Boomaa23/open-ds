package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.util.SequenceCounter;

public abstract class PacketCreator implements MainJDEC {
    protected static final SequenceCounter SEQUENCE_COUNTER = resetSequenceCounter();

    public abstract byte[] dsToRioUdp();
    public abstract byte[] dsToRioTcp();
    public abstract byte[] dsToFmsUdp();
    public abstract byte[] dsToFmsTcp();

    public static PacketBuilder getSequenced() {
        return new PacketBuilder().addBytes(SEQUENCE_COUNTER.increment().getBytes());
    }

    public static SequenceCounter resetSequenceCounter() {
        return new SequenceCounter(true);
    }
}
