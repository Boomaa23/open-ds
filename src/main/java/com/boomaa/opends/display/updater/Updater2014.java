package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2014;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.util.NumberUtils;

public class Updater2014 extends ElementUpdater {
    @Override
    protected void doUpdateFromRioUdp(PacketParser data, TVMList tagMap) {
        Parser2014.RioToDsUdp rioUdp = (Parser2014.RioToDsUdp) data;
        ESTOP_STATUS.setDisplay(rioUdp.isEmergencyStopped());
        BAT_VOLTAGE.setText(NumberUtils.padDouble(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2), 2) + " V");
        ROBOT_CONNECTION_STATUS.forceDisplay();
    }

    @Override
    protected void doUpdateFromRioTcp(PacketParser data, TVMList tagMap) {
        // No TCP connections in LibDS
    }

    @Override
    protected void doUpdateFromFmsUdp(PacketParser data, TVMList tagMap) {
        Parser2014.FmsToDsUdp fmsUdp = (Parser2014.FmsToDsUdp) data;
        AllianceStation station = fmsUdp.getAllianceStation();
        if (station != null) {
            ALLIANCE_COLOR.setEnabled(false);
            ALLIANCE_COLOR.setSelectedItem(station.isBlue() ? "Blue" : "Red");
            ALLIANCE_NUM.setEnabled(false);
            ALLIANCE_NUM.setSelectedItem(station.getSidedNum());
        }
        FMS_CONNECTION_STATUS.forceDisplay();
    }

    @Override
    protected void doUpdateFromFmsTcp(PacketParser data, TVMList tagMap) {
        // No TCP connections in LibDS
    }

    @Override
    protected void resetDataRioUdp() {
        ESTOP_STATUS.forceHide();
        BAT_VOLTAGE.setText("0.00 V");
        ROBOT_CONNECTION_STATUS.forceHide();
    }

    @Override
    protected void resetDataRioTcp() {
        // No TCP connections in LibDS
        PacketCreator.SEQUENCE_COUNTER_RIO.reset();
    }

    @Override
    protected void resetDataFmsUdp() {
        FMS_CONNECTION_STATUS.forceHide();
        ALLIANCE_COLOR.setEnabled(true);
        ALLIANCE_NUM.setEnabled(true);
    }

    @Override
    protected void resetDataFmsTcp() {
        // No TCP connections in LibDS
        PacketCreator.SEQUENCE_COUNTER_FMS.reset();
    }
}
