package com.boomaa.opends.util;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

public class PacketCounters {
    public static final SequenceCounter NULL_COUNTER = new SequenceCounter(false, 0, false);
    public static final SequenceCounter ROBO_RIO_TCP = new SequenceCounter(false);
    public static final SequenceCounter ROBO_RIO_UDP = new SequenceCounter(false);
    public static final SequenceCounter FMS_TCP = new SequenceCounter(false);
    public static final SequenceCounter FMS_UDP = new SequenceCounter(false);

    public static SequenceCounter get(Remote remote, Protocol protocol) {
        try {
            Object counter = PacketCounters.class.getDeclaredField(remote.name() + "_" + protocol.name()).get(null);
            return counter != null ? (SequenceCounter) counter : NULL_COUNTER;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return NULL_COUNTER;
    }
}
