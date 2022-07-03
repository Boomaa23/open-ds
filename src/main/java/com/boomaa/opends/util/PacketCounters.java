package com.boomaa.opends.util;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

public class PacketCounters {
    public static final SequenceCounter ROBO_RIO_TCP = new SequenceCounter(false);
    public static final SequenceCounter ROBO_RIO_UDP = new SequenceCounter(false);
    public static final SequenceCounter FMS_TCP = new SequenceCounter(false);
    public static final SequenceCounter FMS_UDP = new SequenceCounter(false);

    public static SequenceCounter get(Remote remote, Protocol protocol) {
        boolean ptcp = protocol == Protocol.TCP;
        return remote == Remote.ROBO_RIO ?
                (ptcp ? ROBO_RIO_TCP : ROBO_RIO_UDP) :
                (ptcp ? FMS_TCP : FMS_UDP);
    }
}
