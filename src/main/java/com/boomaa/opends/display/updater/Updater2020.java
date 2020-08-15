package com.boomaa.opends.display.updater;

import com.boomaa.opends.data.Challenge;
import com.boomaa.opends.data.holders.Status;
import com.boomaa.opends.data.holders.Trace;
import com.boomaa.opends.data.receive.ReceiveTag;
import com.boomaa.opends.data.receive.TVMList;
import com.boomaa.opends.data.receive.TagValueMap;
import com.boomaa.opends.data.receive.parser.PacketParser;
import com.boomaa.opends.data.receive.parser.Parser2020;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.StatsFrame;
import com.boomaa.opends.util.NumberUtils;

public class Updater2020 extends ElementUpdater {
    @Override
    protected void doUpdateFromRioUdp(PacketParser data, TVMList tagMap) {
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

        if (tagMap.size() > 0) {
            TVMList dinf = tagMap.getMatching(ReceiveTag.DISK_INFO);
            if (!dinf.isEmpty()) {
                TagValueMap<?> diskInfo = dinf.first();
                StatsFrame.EmbeddedJDEC.DISK_SPACE.setText(NumberUtils.bytesHumanReadable((Integer) diskInfo.get("Free Space")));
            }
            TVMList rinf = tagMap.getMatching(ReceiveTag.RAM_INFO);
            if (!rinf.isEmpty()) {
                TagValueMap<?> ramInfo = rinf.first();
                StatsFrame.EmbeddedJDEC.RAM_SPACE.setText(NumberUtils.bytesHumanReadable((Integer) ramInfo.get("Free Space")));
            }
            TVMList cif = tagMap.getMatching(ReceiveTag.CPU_INFO);
            if (!cif.isEmpty()) {
                TagValueMap<?> cpuInfo = cif.first();
                double cpuPercent = 0;
                int numCpus = (Integer) cpuInfo.get("Number of CPUs");
                for (int i = 1; i <= numCpus; i++) {
                    //TODO test if this cpu percentage algorithm works
                    float tCrit = (Float) cpuInfo.get("CPU " + i + " Time Critical %");
                    float tAbove = (Float) cpuInfo.get("CPU " + i + " Above Normal %");
                    float tNorm = (Float) cpuInfo.get("CPU " + i + " Normal %");
                    float tLow = (Float) cpuInfo.get("CPU " + i + " Low %");
                    cpuPercent += (tCrit + (tAbove * 0.90) + (tNorm * 0.75) + (tLow * 0.25))
                            / (tCrit + tAbove + tNorm + tLow);
                }
                cpuPercent /= numCpus;
                StatsFrame.EmbeddedJDEC.CPU_PERCENT.setText(String.valueOf(cpuPercent));
            }

            TVMList canm = tagMap.getMatching(ReceiveTag.CAN_METRICS);
            if (!canm.isEmpty()) {
                TagValueMap<?> canMetrics = canm.first();
                StatsFrame.EmbeddedJDEC.CAN_UTILIZATION.setText(String.valueOf(canMetrics.get("Utilization %")));
                StatsFrame.EmbeddedJDEC.CAN_BUS_OFF.setText(String.valueOf(canMetrics.get("Bus Off")));
                StatsFrame.EmbeddedJDEC.CAN_TX_FULL.setText(String.valueOf(canMetrics.get("TX Full")));
                StatsFrame.EmbeddedJDEC.CAN_RX_ERR.setText(String.valueOf(canMetrics.get("RX Errors")));
                StatsFrame.EmbeddedJDEC.CAN_TX_ERR.setText(String.valueOf(canMetrics.get("TX Errors")));
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
                StatsFrame.EmbeddedJDEC.DISABLE_FAULTS_COMMS.setText(String.valueOf(disableFaults.get("Comms")));
                StatsFrame.EmbeddedJDEC.DISABLE_FAULTS_12V.setText(String.valueOf(disableFaults.get("12V")));
            }
            TVMList rf = tagMap.getMatching(ReceiveTag.RAIL_FAULTS);
            if (!rf.isEmpty()) {
                TagValueMap<?> railFaults = rf.first();
                StatsFrame.EmbeddedJDEC.RAIL_FAULTS_6V.setText(String.valueOf(railFaults.get("6V")));
                StatsFrame.EmbeddedJDEC.RAIL_FAULTS_5V.setText(String.valueOf(railFaults.get("5V")));
                StatsFrame.EmbeddedJDEC.RAIL_FAULTS_3P3V.setText(String.valueOf(railFaults.get("3.3V")));
            }

            TVMList versionInfo = tagMap.getMatching(ReceiveTag.VERSION_INFO);
            if (!versionInfo.isEmpty()) {
                boolean rioSet = false;
                boolean wpiLibSet = false;
                for (TagValueMap<?> tvm : versionInfo) {
                    String name = (String) tvm.get("Name");
                    if (name.equals("roboRIO Image")) {
                        StatsFrame.EmbeddedJDEC.ROBORIO_VERSION.setText(String.valueOf(tvm.get("Version")));
                        rioSet = true;
                    } else if (name.equals("FRC_Lib_Version")) {
                        StatsFrame.EmbeddedJDEC.WPILIB_VERSION.setText(String.valueOf(tvm.get("Version")));
                        wpiLibSet = true;
                    }
                    if (rioSet && wpiLibSet) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void doUpdateFromFmsUdp(PacketParser data, TVMList tagMap) {
        //TODO add fms content
    }

    @Override
    protected void doUpdateFromFmsTcp(PacketParser data, TVMList tagMap) {
        //TODO add fms content
        TVMList challenge = tagMap.getMatching(ReceiveTag.CHALLENGE_QUESTION);
        if (challenge != null) {
            int value = (Integer) challenge.first().get("Challenge Value");
            int teamNum = Integer.parseInt(TEAM_NUMBER.getText());
            CHALLENGE_RESPONSE.setText(Challenge.getResponse(value, teamNum));
        }
    }

    @Override
    protected void resetDataRioUdp() {
        BAT_VOLTAGE.setText("0.00 V");
        ROBOT_CONNECTION_STATUS.forceHide();
        ROBOT_CODE_STATUS.forceHide();
        BROWNOUT_STATUS.forceHide();

        StatsFrame.EmbeddedJDEC.DISK_SPACE.setText("");
        StatsFrame.EmbeddedJDEC.RAM_SPACE.setText("");
        StatsFrame.EmbeddedJDEC.CPU_PERCENT.setText("");
        StatsFrame.EmbeddedJDEC.CAN_UTILIZATION.setText("");
        StatsFrame.EmbeddedJDEC.CAN_BUS_OFF.setText("");
        StatsFrame.EmbeddedJDEC.CAN_TX_FULL.setText("");
        StatsFrame.EmbeddedJDEC.CAN_RX_ERR.setText("");
        StatsFrame.EmbeddedJDEC.CAN_TX_ERR.setText("");
    }

    @Override
    protected void resetDataRioTcp() {
        StatsFrame.EmbeddedJDEC.DISABLE_FAULTS_COMMS.setText("");
        StatsFrame.EmbeddedJDEC.DISABLE_FAULTS_12V.setText("");
        StatsFrame.EmbeddedJDEC.RAIL_FAULTS_6V.setText("");
        StatsFrame.EmbeddedJDEC.RAIL_FAULTS_5V.setText("");
        StatsFrame.EmbeddedJDEC.RAIL_FAULTS_3P3V.setText("");
        StatsFrame.EmbeddedJDEC.ROBORIO_VERSION.setText("");
        StatsFrame.EmbeddedJDEC.WPILIB_VERSION.setText("");
    }

    @Override
    protected void resetDataFmsUdp() {
        //TODO add fms content
    }

    @Override
    protected void resetDataFmsTcp() {
        CHALLENGE_RESPONSE.setText("");
        //TODO add fms content
    }
}
