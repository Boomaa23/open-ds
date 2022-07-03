package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2015;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.tabs.JoystickTab;
import com.boomaa.opends.display.tabs.TabBase;
import com.boomaa.opends.util.NumberUtils;
import com.boomaa.opends.util.StringUtils;

import java.util.List;

public class Updater2015 extends ElementUpdater {
    @Override
    protected void doUpdateFromRioUdp(PacketParser data, TVMList tagMap) {
        Parser2015.RioToDsUdp rioUdp = (Parser2015.RioToDsUdp) data;
        ESTOP_STATUS.setDisplay(rioUdp.getStatus().contains(Status.ESTOP));
        if (rioUdp.getStatus().contains(Status.CODE_INIT)) {
            ROBOT_CODE_STATUS.changeToDisplay(1, true);
        } else {
            ROBOT_CODE_STATUS.forceHide();
        }
        BAT_VOLTAGE.setText(StringUtils.padDouble(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2), 2) + " V");
        FMS_CONNECTION_STATUS.forceDisplay();

        if (tagMap.size() > 0) {
            TVMList dinf = tagMap.getMatching(ReceiveTag.DISK_INFO);
            if (!dinf.isEmpty()) {
                StatsFields.DISK_SPACE.updateTableValue(dinf.first().get("Utilization %"));
            }
            TVMList rinf = tagMap.getMatching(ReceiveTag.RAM_INFO);
            if (!rinf.isEmpty()) {
                StatsFields.RAM_SPACE.updateTableValue(rinf.first().get("Utilization %"));
            }
            TVMList cif = tagMap.getMatching(ReceiveTag.CPU_INFO);
            if (!cif.isEmpty()) {
                StatsFields.CPU_PERCENT.updateTableValue(cif.first().get("Utilization %"));
            }

            TVMList canm = tagMap.getMatching(ReceiveTag.CAN_METRICS);
            if (!canm.isEmpty()) {
                StatsFields.CAN_UTILIZATION.updateTableValue(canm.first().get("Utilization %"));
            }
        }
    }

    @Override
    protected void doUpdateFromRioTcp(PacketParser data, TVMList tagMap) {
        // No TCP connections in LibDS
    }

    @Override
    protected void doUpdateFromFmsUdp(PacketParser data, TVMList tagMap) {
        Parser2015.FmsToDsUdp fmsUdp = (Parser2015.FmsToDsUdp) data;
        List<Control> control = fmsUdp.getControl();
        if (control.contains(Control.ENABLED) && !TabBase.isVisible(JoystickTab.class)) {
            IS_ENABLED.setSelected(true);
            IS_ENABLED.setEnabled(false);
        }
        for (RobotMode mode : RobotMode.values()) {
            if (control.contains(mode.getControlFlag())) {
                ROBOT_DRIVE_MODE.setSelectedItem(mode);
                break;
            }
        }
        AllianceStation station = fmsUdp.getAllianceStation();
        if (station != null) {
            ALLIANCE_COLOR.setEnabled(false);
            ALLIANCE_COLOR.setSelectedItem(station.isBlue() ? "Blue" : "Red");
            ALLIANCE_NUM.setEnabled(false);
            ALLIANCE_NUM.setSelectedItem(station.getSidedNum());
        }
    }

    @Override
    protected void doUpdateFromFmsTcp(PacketParser data, TVMList tagMap) {
        // No TCP connections in LibDS
    }

    @Override
    protected void resetDataRioUdp() {
        BAT_VOLTAGE.setText("0.00 V");
        ROBOT_CODE_STATUS.forceHide();
        ESTOP_STATUS.forceHide();
    }

    @Override
    protected void resetDataRioTcp() {
        // No TCP connections in LibDS
        PacketCreator.SEQUENCE_COUNTER_RIO.reset();
    }

    @Override
    protected void resetDataFmsUdp() {
        IS_ENABLED.setSelected(false);
        IS_ENABLED.setEnabled(true);
        ALLIANCE_COLOR.setEnabled(true);
        ALLIANCE_NUM.setEnabled(true);
        FMS_CONNECTION_STATUS.forceHide();
    }

    @Override
    protected void resetDataFmsTcp() {
        // No TCP connections in LibDS
        PacketCreator.SEQUENCE_COUNTER_FMS.reset();
    }
}
