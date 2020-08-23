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
        if (data instanceof ParserNull) {
            resetDataRioUdp();
        } else {
            doUpdateFromRioUdp(data, data.getTags());
            doLog(data);
        }
    }

    public void updateFromRioTcp(PacketParser data) {
        if (data instanceof ParserNull) {
            resetDataRioTcp();
        } else {
            doUpdateFromRioTcp(data, data.getTags());
            doLog(data);
        }
    }

    public void updateFromFmsUdp(PacketParser data) {
        if (data instanceof ParserNull) {
            resetDataFmsUdp();
        } else {
            doUpdateFromFmsUdp(data, data.getTags());
            doLog(data);
        }
    }

    public void updateFromFmsTcp(PacketParser data) {
        if (data instanceof ParserNull) {
            resetDataFmsTcp();
        } else {
            doUpdateFromFmsTcp(data, data.getTags());
            doLog(data);
        }
    }

    private void doLog(PacketParser data) {
        for (TagValueMap<?> tvm : data.getTags()) {
            if (tvm.getBaseTag().includeInLog()) {
                Logger.OUT.println(tvm.toLogString(true));
            }
            //TODO conditional logger output
        }
    }
}
