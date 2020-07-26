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
        BROWNOUT_STATUS.setDisplay(rioUdp.getStatus().contains(Status.ESTOP));
        if (rioUdp.getTrace().contains(Trace.ROBOTCODE)) {
            ROBOT_CODE_STATUS.changeToDisplay(0, true);
        } else if (rioUdp.getStatus().contains(Status.CODE_INIT)) {
            ROBOT_CODE_STATUS.changeToDisplay(1, true);
        } else {
            ROBOT_CODE_STATUS.forceHide();
        }
        boolean robotConn = rioUdp.getTrace().contains(Trace.ISROBORIO);
        ROBOT_CONNECTION_STATUS.setDisplay(robotConn);
        IS_ENABLED.setEnabled(robotConn);
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
