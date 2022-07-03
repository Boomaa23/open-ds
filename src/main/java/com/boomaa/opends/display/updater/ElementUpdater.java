package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.ParserNull;
import com.boomaa.opends.display.Logger;
import com.boomaa.opends.display.MainJDEC;

public abstract class ElementUpdater implements MainJDEC {
    protected abstract void doUpdateFromRioUdp(PacketParser data, TVMList tagMap);
    protected abstract void doUpdateFromRioTcp(PacketParser data, TVMList tagMap);
    protected abstract void doUpdateFromFmsUdp(PacketParser data, TVMList tagMap);
    protected abstract void doUpdateFromFmsTcp(PacketParser data, TVMList tagMap);

    protected abstract void resetDataRioUdp();
    protected abstract void resetDataRioTcp();
    protected abstract void resetDataFmsUdp();
    protected abstract void resetDataFmsTcp();

    public void update(PacketParser data, Remote remote, Protocol protocol) {
        if (data instanceof ParserNull) {
            data.getPacketCounter().reset();
            reset(remote, protocol);
        } else {
            data.getPacketCounter().increment();
            boolean ptcp = protocol == Protocol.TCP;
            TVMList tagMap = data.getTags();
            if (remote == Remote.ROBO_RIO) {
                if (ptcp) {
                    doUpdateFromRioTcp(data, tagMap);
                } else {
                    doUpdateFromRioUdp(data, tagMap);
                }
            } else {
                if (ptcp) {
                    doUpdateFromFmsTcp(data, tagMap);
                } else {
                    doUpdateFromFmsUdp(data, tagMap);
                }
            }
            log(data);
        }
    }

    public void reset(Remote remote, Protocol protocol) {
        boolean ptcp = protocol == Protocol.TCP;
        if (remote == Remote.ROBO_RIO) {
            if (ptcp) {
                resetDataRioTcp();
            } else {
                resetDataRioUdp();
            }
        } else {
            if (ptcp) {
                resetDataFmsTcp();
            } else {
                resetDataFmsUdp();
            }
        }
    }

    public void log(PacketParser data) {
        for (TagValueMap<?> tvm : data.getTags()) {
            if (tvm.getBaseTag().includeInLog()) {
                Logger.OUT.println(tvm.toLogString(true));
            }
        }
    }
}
