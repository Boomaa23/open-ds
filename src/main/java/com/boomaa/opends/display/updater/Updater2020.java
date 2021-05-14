package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.Challenge;
import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.data.send.creator.PacketCreator;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.util.DSLog;
import com.boomaa.opends.util.NumberUtils;

import java.util.List;

public class Updater2020 extends ElementUpdater {
    @Override
    protected void doUpdateFromRioUdp(PacketParser data, TVMList tagMap) {
        Parser2020.RioToDsUdp rioUdp = (Parser2020.RioToDsUdp) data;
        IS_ENABLED.setEnabled(true);
        ESTOP_STATUS.setDisplay(rioUdp.getStatus().contains(Status.ESTOP));
        if (rioUdp.getTrace().contains(Trace.ROBOTCODE)) {
            ROBOT_CODE_STATUS.changeToDisplay(0, true);
        } else if (rioUdp.getStatus().contains(Status.CODE_INIT)) {
            ROBOT_CODE_STATUS.changeToDisplay(1, true);
        } else {
            ROBOT_CODE_STATUS.forceHide();
        }

        boolean robotConn = DisplayEndpoint.NET_IF_INIT.isOrInit(Remote.ROBO_RIO);
        if (robotConn) {
            boolean isRealRobot = rioUdp.getTrace().contains(Trace.ISROBORIO);
            ROBOT_CONNECTION_STATUS.changeToDisplay(isRealRobot ? 0 : 1, true);
        } else {
            ROBOT_CONNECTION_STATUS.forceHide();
        }

        BAT_VOLTAGE.setText(NumberUtils.padDouble(NumberUtils.roundTo(rioUdp.getBatteryVoltage(), 2), 2) + " V");

        if (tagMap.size() > 0) {
            TVMList dinf = tagMap.getMatching(ReceiveTag.DISK_INFO);
            if (!dinf.isEmpty()) {
                TagValueMap<?> diskInfo = dinf.first();
                StatsFields.DISK_SPACE.updateTableValue(NumberUtils.bytesHumanReadable((Integer) diskInfo.get("Free Space")));
            }
            TVMList rinf = tagMap.getMatching(ReceiveTag.RAM_INFO);
            if (!rinf.isEmpty()) {
                TagValueMap<?> ramInfo = rinf.first();
                StatsFields.RAM_SPACE.updateTableValue(NumberUtils.bytesHumanReadable((Integer) ramInfo.get("Free Space")));
            }
            TVMList cif = tagMap.getMatching(ReceiveTag.CPU_INFO);
            if (!cif.isEmpty()) {
                TagValueMap<?> cpuInfo = cif.first();
                double cpuPercent = 0;
                float numCpus = (Float) cpuInfo.get("Number of CPUs");
                for (int i = 0; i < numCpus; i++) {
                    //TODO test if this cpu percentage algorithm works
                    float tCrit = (Float) cpuInfo.get("CPU " + i + " Time Critical %");
                    float tAbove = (Float) cpuInfo.get("CPU " + i + " Above Normal %");
                    float tNorm = (Float) cpuInfo.get("CPU " + i + " Normal %");
                    float tLow = (Float) cpuInfo.get("CPU " + i + " Low %");
                    cpuPercent += (tCrit + (tAbove * 0.90) + (tNorm * 0.75) + (tLow * 0.25))
                            / (tCrit + tAbove + tNorm + tLow);
                }
                cpuPercent /= numCpus;
                StatsFields.CPU_PERCENT.updateTableValue(cpuPercent);
            }

            TVMList canm = tagMap.getMatching(ReceiveTag.CAN_METRICS);
            if (!canm.isEmpty()) {
                TagValueMap<?> canMetrics = canm.first();
                StatsFields.CAN_UTILIZATION.updateTableValue(canMetrics.get("Utilization %"));
                StatsFields.CAN_BUS_OFF.updateTableValue(canMetrics.get("Bus Off"));
                StatsFields.CAN_TX_FULL.updateTableValue(canMetrics.get("TX Full"));
                StatsFields.CAN_RX_ERR.updateTableValue(canMetrics.get("RX Errors"));
                StatsFields.CAN_TX_ERR.updateTableValue(canMetrics.get("TX Errors"));
            }
            //TODO add rumbler capability (JInput != XInput compatible)
        }
    }

    @Override
    protected void doUpdateFromRioTcp(PacketParser data, TVMList tagMap) {
        if (tagMap.size() > 0) {
            TVMList dfm = tagMap.getMatching(ReceiveTag.DISABLE_FAULTS);
            if (!dfm.isEmpty()) {
                TagValueMap<?> disableFaults = dfm.first();
                StatsFields.DISABLE_FAULTS_COMMS.updateTableValue(disableFaults.get("Comms"));
                StatsFields.DISABLE_FAULTS_12V.updateTableValue(disableFaults.get("12V"));
            }
            TVMList rf = tagMap.getMatching(ReceiveTag.RAIL_FAULTS);
            if (!rf.isEmpty()) {
                TagValueMap<?> railFaults = rf.first();
                StatsFields.RAIL_FAULTS_6V.updateTableValue(railFaults.get("6V"));
                StatsFields.RAIL_FAULTS_5V.updateTableValue(railFaults.get("5V"));
                StatsFields.RAIL_FAULTS_3P3V.updateTableValue(railFaults.get("3.3V"));
            }

            TVMList versionInfo = tagMap.getMatching(ReceiveTag.VERSION_INFO);
            if (!versionInfo.isEmpty()) {
                boolean rioSet = false;
                boolean wpiLibSet = false;
                for (TagValueMap<?> tvm : versionInfo) {
                    String name = (String) tvm.get("Name");
                    if (name.equals("roboRIO Image")) {
                        StatsFields.ROBORIO_VERSION.updateTableValue(tvm.get("Version"));
                        rioSet = true;
                    } else if (name.equals("FRC_Lib_Version")) {
                        StatsFields.WPILIB_VERSION.updateTableValue(tvm.get("Version"));
                        wpiLibSet = true;
                    }
                    if (rioSet && wpiLibSet) {
                        break;
                    }
                }
            }

            TVMList em = tagMap.getMatching(ReceiveTag.ERROR_MESSAGE);
            if (!em.isEmpty()) {
                TagValueMap<?> errorMessage = em.first();
                String error = (String) errorMessage.get("Details") + errorMessage.get("Location") + errorMessage.get("Call Stack");
                String flag = (String) errorMessage.get("Flag");
                DSLog.queueEvent(error, flag != null && flag.equals("Error") ? DSLog.EventSeverity.ERROR : DSLog.EventSeverity.WARNING);
            }

            TVMList so = tagMap.getMatching(ReceiveTag.STANDARD_OUT);
            if (!so.isEmpty()) {
                TagValueMap<?> stdOut = so.first();
                DSLog.queueEvent((String) stdOut.get("Message"), DSLog.EventSeverity.INFO);
            }
        }
    }

    @Override
    protected void doUpdateFromFmsUdp(PacketParser data, TVMList tagMap) {
        Parser2020.FmsToDsUdp fmsUdp = (Parser2020.FmsToDsUdp) data;
        AllianceStation station = fmsUdp.getAllianceStation();
        List<Control> ctrlSignals = fmsUdp.getControl();
        for (RobotMode mode : RobotMode.values()) {
            if (ctrlSignals.contains(mode.getControlFlag())) {
                ROBOT_DRIVE_MODE.setSelectedItem(mode.getControlFlag());
            }
        }
        if (!station.equals(new AllianceStation(ALLIANCE_NUM.getSelectedIndex() - 1,
                ALLIANCE_COLOR.getSelectedItem().equals("Blue")))) {
            ALLIANCE_COLOR.setEnabled(true);
            ALLIANCE_COLOR.setSelectedItem(station.isBlue() ? "Blue" : "Red");
            ALLIANCE_COLOR.setEnabled(false);
            ALLIANCE_NUM.setEnabled(true);
            ALLIANCE_NUM.setSelectedItem(station.getSidedNum());
            ALLIANCE_NUM.setEnabled(false);
        }
        FMS_CONNECTION_STATUS.forceDisplay();
        IS_ENABLED.setEnabled(false);
        ROBOT_DRIVE_MODE.setEnabled(false);
        IS_ENABLED.setSelected(fmsUdp.getControl().contains(Control.ENABLED));
    }

    @Override
    protected void doUpdateFromFmsTcp(PacketParser data, TVMList tagMap) {
        TVMList challenge = tagMap.getMatching(ReceiveTag.CHALLENGE_QUESTION);
        if (!challenge.isEmpty()) {
            int value = (Integer) challenge.first().get("Challenge Value");
            int teamNum = Integer.parseInt(TEAM_NUMBER.getText());
            CHALLENGE_RESPONSE.setText(Challenge.getResponse(value, teamNum));
        }
    }

    @Override
    protected void resetDataRioUdp() {
        IS_ENABLED.setSelected(false);
        IS_ENABLED.setEnabled(false);
        BAT_VOLTAGE.setText("0.00 V");
        ROBOT_CONNECTION_STATUS.forceHide();
        ROBOT_CODE_STATUS.forceHide();
        ESTOP_STATUS.forceHide();
    }

    @Override
    protected void resetDataRioTcp() {
        PacketCreator.SEQUENCE_COUNTER_RIO.reset();
    }

    @Override
    protected void resetDataFmsUdp() {
        MATCH_TIME.forceHide();
        ALLIANCE_COLOR.setEnabled(true);
        ALLIANCE_NUM.setEnabled(true);
        FMS_CONNECTION_STATUS.forceHide();
        ROBOT_DRIVE_MODE.setEnabled(true);
    }

    @Override
    protected void resetDataFmsTcp() {
        CHALLENGE_RESPONSE.setText("");
        PacketCreator.SEQUENCE_COUNTER_FMS.reset();
    }
}
