package com.boomaa.opends.util;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.networking.WlanConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DSLog extends Clock {
    private FileOutputStream output;
    public static byte[] PDP_STATS = new byte[24];

    public DSLog() {
        super(20);
        Calendar date = Calendar.getInstance();
        String weekday = Date.DayMap.getFromInt(date.get(Calendar.DAY_OF_WEEK) - 1).name();
        String folderName = OperatingSystem.isWindows() ? "C:\\Users\\Public\\Documents\\FRC\\Log Files\\" : "/var/log/opends/";
        File folder = new File(folderName);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        String filepath = folderName + date.get(Calendar.YEAR) + "_" + NumberUtils.padInt(date.get(Calendar.MONTH) + 1, 2) + "_" + NumberUtils.padInt(date.get(Calendar.DAY_OF_MONTH), 2)
                + " " + NumberUtils.padInt(date.get(Calendar.HOUR_OF_DAY), 2) + "_" + NumberUtils.padInt(date.get(Calendar.MINUTE), 2) + "_" + NumberUtils.padInt(date.get(Calendar.SECOND), 2)
                + " " + weekday.charAt(0) + weekday.substring(1, 3).toLowerCase() + ".dslog";
        File logFile = new File(filepath);
        if (!logFile.isFile()) {
            try {
                logFile.createNewFile();
                this.output = new FileOutputStream(logFile);
            } catch (IOException e) {
                MessageBox.show("Could not create log file", MessageBox.Type.ERROR);
                this.interrupt();
                return;
            }
        }
        writeData(new PacketBuilder()
                .pad(0x00, 3)
                .addInt(0x03) // only observed value
                .pad(0x00, 4)
                .addBytes(NumberUtils.intToByteQuad((int) (-2_212_122_495L + (System.currentTimeMillis() / 1000))))
                        // seconds 1904 to 1970 (labview/unix epoch) + after
                .addInt((int) (System.currentTimeMillis() % 1000) * 256)
                .pad(0x00, 7)
                .build()
        );
    }

    @Override
    public void onCycle() {
        //TODO figure out diff b/n DS_mode and ROBOT_mode traces
        int trace = 0xFF;
        WlanConnection radio = WlanConnection.getRadio();
        double bat = checkedNumParse(MainJDEC.BAT_VOLTAGE.getText().replaceAll(" V", ""));

        if (DisplayEndpoint.NET_IF_INIT.isOrInit(Remote.ROBO_RIO)) {
            if (MainJDEC.IS_ENABLED.isSelected()) {
                RobotMode selMode = (RobotMode) MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem();
                if (selMode == RobotMode.TELEOPERATED) {
                    trace -= Trace.DS_TELEOP.flag + Trace.ROBOT_TELEOP.flag;
                } else if (selMode == RobotMode.AUTONOMOUS) {
                    trace -= Trace.DS_AUTO.flag + Trace.ROBOT_AUTO.flag;
                }
            } else {
                trace -= Trace.DS_DISABLED.flag + Trace.ROBOT_DISABLED.flag;
            }
            // < 6.8 volts = roboRIO brownout voltage
            if (bat < 6.8) {
                trace -= Trace.BROWNOUT.flag;
            }
        }

        writeData(new PacketBuilder()
                .addInts(getTripTime(0x00), getPacketLoss(0x00))
                .addBytes(getBattery(bat))
                .addInts(getRioCPU((int) checkedNumParse(StatsFields.CPU_PERCENT.getValue())), trace,
                        getCAN((int) checkedNumParse(StatsFields.CAN_UTILIZATION.getValue())),
                        getWifiDb(radio != null ? radio.getSignal() : 0x00),
                        getWifiMb(0x00)) //wifiMb
                .pad(0x00, 2)
                .addBytes(PDP_STATS)
                .build()
        );
    }

    private void writeData(byte[] data) {
        try {
            output.write(data);
            output.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private double checkedNumParse(String in) {
        try {
            return Double.parseDouble(in);
        } catch (NumberFormatException ignored) {
            return 0D;
        }
    }

    private int getTripTime(int tripTime) {
        return tripTime * 15;
    }

    private int getPacketLoss(int packetLoss) {
        return packetLoss / 4;
    }

    private byte[] getBattery(double bat) {
        byte[] out = new byte[2];
        out[0] = (byte) ((int) bat);
        out[1] = (byte) ((int) ((bat - out[0]) * 256));
        return out;
    }

    private int getRioCPU(int rioCPU) {
        return rioCPU * 2;
    }

    private int getCAN(int can) {
        return can * 2;
    }

    private int getWifiDb(int wifiDb) {
        return wifiDb * 2;
    }

    private int getWifiMb(int wifiMb) {
        return wifiMb * 2;
    }

    private enum Trace {
        BROWNOUT(0x80),
        WATCHDOG(0x40),
        DS_TELEOP(0x20),
        DS_AUTO(0x10),
        DS_DISABLED(0x08),
        ROBOT_TELEOP(0x04),
        ROBOT_AUTO(0x02),
        ROBOT_DISABLED(0x01);

        private final int flag;

        Trace(int flag) {
            this.flag = flag;
        }
    }
}
