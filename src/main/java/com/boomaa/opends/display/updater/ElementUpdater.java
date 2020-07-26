package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.display.MainJDEC;

public abstract class ElementUpdater implements MainJDEC {
    public abstract void updateFromRioUdp(PacketParser data);
    public abstract void updateFromRioTcp(PacketParser data);
    public abstract void updateFromFmsUdp(PacketParser data);
    public abstract void updateFromFmsTcp(PacketParser data);
}
