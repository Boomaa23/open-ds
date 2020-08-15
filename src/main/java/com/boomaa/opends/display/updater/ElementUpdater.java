package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.data.receive.parser.PacketParser;
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
        if (data.getPacket().length != 0) {
            doUpdateFromRioUdp(data, data.getTags());
            doLog(data);
        } else {
            resetDataRioUdp();
        }
    }

    public void updateFromRioTcp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromRioTcp(data, data.getTags());
            doLog(data);
        } else {
            resetDataRioTcp();
        }
    }

    public void updateFromFmsUdp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromFmsUdp(data, data.getTags());
            doLog(data);
        } else {
            resetDataFmsUdp();
        }
    }

    public void updateFromFmsTcp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromFmsTcp(data, data.getTags());
            doLog(data);
        } else {
            resetDataFmsTcp();
        }
    }

    private void doLog(PacketParser data) {
        for (TagValueMap<?> tvm : data.getTags()) {
            if (tvm.getBaseTag().includeInLog() == Logger.Include.ALWAYS) {
                Logger.OUT.println(tvm.toLogString(true));
            }
            //TODO conditional logger output
        }
    }
}
