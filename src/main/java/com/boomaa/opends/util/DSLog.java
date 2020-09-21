package com.boomaa.opends.util;

import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.display.frames.StatsFrame;
import com.boomaa.opends.networking.WlanConnection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DSLog extends Clock {
    private final String filepath;
    public static byte[] PDP_STATS = new byte[24];

    public DSLog() {
        super(1000);
        Calendar date = Calendar.getInstance();
        String weekday = Date.DayMap.getFromInt(date.get(Calendar.DAY_OF_WEEK), false).name();
        String folderName = OperatingSystem.getCurrent() == OperatingSystem.WINDOWS ? "C:\\Users\\Public\\Documents\\FRC\\Log Files\\" : "/var/log/opends/";
        File folder = new File(folderName);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        this.filepath = folderName + date.get(Calendar.YEAR) + "_" + NumberUtils.padInt(date.get(Calendar.MONTH) + 1, 2) + "_" + NumberUtils.padInt(date.get(Calendar.DAY_OF_MONTH), 2)
                + " " + NumberUtils.padInt(date.get(Calendar.HOUR_OF_DAY), 2) + "_" + NumberUtils.padInt(date.get(Calendar.MINUTE), 2) + "_" + NumberUtils.padInt(date.get(Calendar.SECOND), 2)
                + " " + weekday.charAt(0) + weekday.substring(1, 3).toLowerCase() + ".dslog";
        File logFile = new File(filepath);
        if (!logFile.isFile()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                MessageBox.show("Could not create log file", MessageBox.Type.ERROR);
                e.printStackTrace();
            }
        }
        writeData(new PacketBuilder()
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
        int trace = (MainJDEC.BROWNOUT_STATUS.isDisplayed() ? Trace.BROWNOUT.flag : 0)
                + (MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem() == RobotMode.TELEOPERATED ? Trace.DS_TELEOP.flag + Trace.ROBOT_TELEOP.flag : 0)
                + (MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem() == RobotMode.AUTONOMOUS ? Trace.ROBOT_AUTO.flag : 0)
                + (!MainJDEC.IS_ENABLED.isSelected() ? Trace.DS_DISABLED.flag + Trace.ROBOT_DISABLED.flag : 0);
        double bat = checkedNumParse(MainJDEC.BAT_VOLTAGE.getText().replaceAll(" V", ""));
        int rioCPU = (int) checkedNumParse(StatsFrame.EmbeddedJDEC.CPU_PERCENT.getText());
        int canUsage = (int) checkedNumParse(StatsFrame.EmbeddedJDEC.CPU_PERCENT.getText());
        int teamNum = (int) checkedNumParse(MainJDEC.TEAM_NUMBER.getText());
        WlanConnection wifi = WlanConnection.getRadio(teamNum);
        writeData(new PacketBuilder()
                .addInts(getTripTime(0x00), trace, getPacketLoss(0x00))
                .addBytes(getBattery(bat))
                .addInts(getRioCPU(rioCPU), getCAN(canUsage),
                        getWifiDb(wifi != null ? wifi.getSignal() : 0x00),
                        0x00) //wifiMb
                .pad(0x00, 2)
                .addBytes(PDP_STATS)
                .build()
        );
    }

    private void writeData(byte[] data) {
        try (FileOutputStream output = new FileOutputStream(filepath, true)) {
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double checkedNumParse(String in) {
        try {
            return Double.parseDouble(in);
        } catch (NumberFormatException ignored) {
            return 0D;
        }
    }

    public int getTripTime(int tripTime) {
        return tripTime * 15;
    }

    public int getPacketLoss(int packetLoss) {
        return packetLoss / 4;
    }

    public byte[] getBattery(double bat) {
        byte[] out = new byte[2];
        out[0] = (byte) ((int) bat);
        out[1] = (byte) ((int) ((bat - out[0]) * 256));
        return out;
    }

    public int getRioCPU(int rioCPU) {
        return rioCPU * 2;
    }

    public int getCAN(int can) {
        return can * 2;
    }

    public int getWifiDb(int wifiDb) {
        return wifiDb * 2;
    }

    public enum Trace {
        BROWNOUT(0x80),
        WATCHDOG(0x40),
        DS_TELEOP(0x20),
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
