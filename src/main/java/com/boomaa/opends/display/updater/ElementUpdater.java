package com.boomaa.opends.display.updater;

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

    public void updateFromRioUdp(PacketParser data) {
        doUpdateGeneric(data);
        if (data instanceof ParserNull) {
            resetDataRioUdp();
        } else {
            data.getPacketCounter().increment();
            doUpdateFromRioUdp(data, data.getTags());
        }
    }

    public void updateFromRioTcp(PacketParser data) {
        doUpdateGeneric(data);
        if (data instanceof ParserNull) {
            resetDataRioTcp();
        } else {
            doUpdateFromRioTcp(data, data.getTags());
        }
    }

    public void updateFromFmsUdp(PacketParser data) {
        doUpdateGeneric(data);
        if (data instanceof ParserNull) {
            resetDataFmsUdp();
        } else {
            doUpdateFromFmsUdp(data, data.getTags());
        }
    }

    public void updateFromFmsTcp(PacketParser data) {
        doUpdateGeneric(data);
        if (data instanceof ParserNull) {
            resetDataFmsTcp();
        } else {
            doUpdateFromFmsTcp(data, data.getTags());
        }
    }

    private void doUpdateGeneric(PacketParser data) {
        if (data instanceof ParserNull) {
            data.getPacketCounter().reset();
        } else {
            data.getPacketCounter().increment();
            doLog(data);
        }
    }

    private void doLog(PacketParser data) {
        for (TagValueMap<?> tvm : data.getTags()) {
            if (tvm.getBaseTag().includeInLog()) {
                Logger.OUT.println(tvm.toLogString(true));
            }
        }
    }
}
