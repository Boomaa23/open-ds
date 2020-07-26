package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.display.MainJDEC;

public abstract class ElementUpdater implements MainJDEC {
    protected abstract void doUpdateFromRioUdp(PacketParser data);
    protected abstract void doUpdateFromRioTcp(PacketParser data);
    protected abstract void doUpdateFromFmsUdp(PacketParser data);
    protected abstract void doUpdateFromFmsTcp(PacketParser data);

    protected abstract void resetDataRioUdp();
    protected abstract void resetDataRioTcp();
    protected abstract void resetDataFmsUdp();
    protected abstract void resetDataFmsTcp();

    public void updateFromRioUdp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromRioUdp(data);
        } else {
            resetDataRioUdp();
        }
    }

    public void updateFromRioTcp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromRioTcp(data);
        } else {
            resetDataRioTcp();
        }
    }

    public void updateFromFmsUdp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromFmsUdp(data);
        } else {
            resetDataFmsUdp();
        }
    }

    public void updateFromFmsTcp(PacketParser data) {
        if (data.getPacket().length != 0) {
            doUpdateFromFmsTcp(data);
        } else {
            resetDataFmsTcp();
        }
    }
}
