package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.util.NumberUtils;

public class Updater2020 extends ElementUpdater {
    @Override
    public void updateFromRioUdp(PacketParser data) {
        Parser2020.RioToDsUdp rioUdp = (Parser2020.RioToDsUdp) data;
        HAS_BROWNOUT.setDisplay(rioUdp.getStatus().contains(Status.ESTOP));
        CODE_INITIALIZING.setDisplay(rioUdp.getStatus().contains(Status.CODE_INIT));
        ROBOT_CODE.setDisplay(rioUdp.getTrace().contains(Trace.ROBOTCODE));
        HAS_ROBOT_CONNECTION.setDisplay(rioUdp.getTrace().contains(Trace.ISROBORIO));
        BAT_VOLTAGE.setText(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2) + " V");
    }

    @Override
    public void updateFromRioTcp(PacketParser data) {

    }

    @Override
    public void updateFromFmsUdp(PacketParser data) {

    }

    @Override
    public void updateFromFmsTcp(PacketParser data) {

    }
}
