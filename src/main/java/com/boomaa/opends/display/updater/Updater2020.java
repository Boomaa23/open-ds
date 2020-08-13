package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.display.Logger;
import com.boomaa.opends.util.NumberUtils;

import java.util.Map;

public class Updater2020 extends ElementUpdater {
    @Override
    protected void doUpdateFromRioUdp(PacketParser data) {
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
        BAT_VOLTAGE.setText(NumberUtils.padNumber(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2), 2) + " V");

        if (Logger.OUT != null) {
            for (Map.Entry<ReceiveTag, TagValueMap<?>> entry : data.getTags().entrySet()) {
                Logger.OUT.println(entry.getValue().toLogString(true));
            }
        }
    }

    @Override
    protected void doUpdateFromRioTcp(PacketParser data) {
        if (Logger.OUT != null) {
            for (Map.Entry<ReceiveTag, TagValueMap<?>> entry : data.getTags().entrySet()) {
                Logger.OUT.println(entry.getValue().toLogString(true));
            }
        }
    }

    @Override
    protected void doUpdateFromFmsUdp(PacketParser data) {
        //TODO add fms content
    }

    @Override
    protected void doUpdateFromFmsTcp(PacketParser data) {
        //TODO add fms content
    }

    @Override
    protected void resetDataRioUdp() {
        BAT_VOLTAGE.setText("0.00 V");
        ROBOT_CONNECTION_STATUS.forceHide();
        ROBOT_CODE_STATUS.forceHide();
        BROWNOUT_STATUS.forceHide();
    }

    @Override
    protected void resetDataRioTcp() {
    }

    @Override
    protected void resetDataFmsUdp() {
        //TODO add fms content
    }

    @Override
    protected void resetDataFmsTcp() {
        //TODO add fms content
    }
}
