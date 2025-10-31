package com.boomaa.opends.util;

import com.boomaa.opends.data.StatsFields;
import com.boomaa.opends.data.holders.Protocol;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DSLog extends Clock {
    private static final long LABVIEW_UNIX_EPOCH_DIFF = -2_212_122_495L;
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss EEE");
    public static byte[] PDP_STATS = new byte[24];
    private static final LockedQueue<byte[]> eventQueue = new LockedQueue<>();
    private final FileOutputStream eventsOut;
    private final FileOutputStream logOut;
    boolean flag = false;

    public DSLog() {
        super(20);
        //TODO fix fully
        String folderName = OperatingSystem.isWindows() ? "C:\\Users\\Public\\Documents\\FRC\\Log Files\\"
                : System.getProperty("user.home") + "/opends/";
        File folder = new File(folderName);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        String filepath = folderName + LocalDateTime.now().format(TS_FORMAT);
        this.logOut = createFile(filepath + ".dslog");
        this.eventsOut = createFile(filepath + ".dsevents");

        final byte[] header = new PacketBuilder()
                .pad(0x00, 3)
                .addInt(0x03) // only observed value
                .pad(0x00, 4)
                .addBytes(secondTimestamp())
                .addBytes(millisecondTimestamp())
                .build();
        writeData(Objects.requireNonNull(logOut), header);
        writeData(Objects.requireNonNull(eventsOut), header);
    }

    private FileOutputStream createFile(String filepath) {
        File outFile = new File(filepath);
        if (!outFile.isFile()) {
            try {
                outFile.createNewFile();
                return new FileOutputStream(outFile);
            } catch (IOException e) {
                MessageBox.show("Could not create log file", MessageBox.Type.ERROR);
                this.end();
            }
        }
        return null;
    }

    @Override
    public void onCycle() {
        //TODO make ROBOT_mode reflect changes in robot connection
        int trace = 0xFF;
        WlanConnection radio = WlanConnection.getRadio();
        double bat = checkedNumParse(MainJDEC.BAT_VOLTAGE.getText().replaceAll(" V", ""));

        if (DisplayEndpoint.NET_IF_INIT.get(Remote.ROBO_RIO, Protocol.TCP)) {
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
        // TODO what does this subtraction do / why is it here?
        if (flag) {
            trace -= Trace.BROWNOUT.flag;
        }
        flag = !flag;

        writeData(logOut, new PacketBuilder()
            .addInts(encodeTripTime(0x00), encodePacketLoss(0x00))
            .addBytes(encodeBattery(bat))
            .addInt(encodeRioCPU((int) checkedNumParse(StatsFields.CPU_PERCENT.getValue())))
            .addInt(trace)
            .addInt(encodeCAN((int) checkedNumParse(StatsFields.CAN_UTILIZATION.getValue())))
            .addInt(encodeWifiDb(radio != null ? radio.getSignal() : 0x00))
            .addBytes(encodeWifiMb(0x00))
            .pad(0x00, 1)
            .addBytes(PDP_STATS)
            .build()
        );

        eventQueue.forEach(e -> writeData(eventsOut, e));
        eventQueue.clear();
    }

    public static void queueEvent(String event, EventSeverity level) {
        event = (level == EventSeverity.ERROR ? level.name() :
                StringUtils.toTitleCase(level.name())) + " " + event;
        byte[] data = new PacketBuilder().pad(0, 4)
                .addBytes(secondTimestamp())
                .addBytes(millisecondTimestamp())
                .addBytes(NumberUtils.intToByteQuad(event.length()))
                .addBytes(event.getBytes())
                .build();
        eventQueue.add(data);
    }

    // Seconds 1904 to 1970 (labview/unix epoch) + after
    private static byte[] secondTimestamp() {
        return NumberUtils.intToByteQuad((int) (LABVIEW_UNIX_EPOCH_DIFF + (int) (System.currentTimeMillis() / 1000)));
    }

    private static byte[] millisecondTimestamp() {
        double ms = (System.currentTimeMillis() % 1000) / 1000.0;
        BigDecimal time = BigDecimal.valueOf(2).pow(64).multiply(BigDecimal.valueOf(ms));
        return NumberUtils.longToByteOctet(time.longValue());
    }

    private void writeData(FileOutputStream out, byte[] data) {
        try {
            out.write(data);
            out.flush();
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

    private int encodeTripTime(int tripTime) {
        return tripTime * 2;
    }

    private int encodePacketLoss(int packetLoss) {
        return packetLoss / 4;
    }

    private byte[] encodeBattery(double bat) {
        byte[] out = new byte[2];
        out[0] = (byte) ((int) bat);
        out[1] = (byte) ((int) ((bat - out[0]) * 256));
        return out;
    }

    private int encodeRioCPU(int rioCPU) {
        return rioCPU * 2;
    }

    private int encodeCAN(int can) {
        return can * 2;
    }

    private int encodeWifiDb(int wifiDb) {
        return wifiDb * 2;
    }

    private byte[] encodeWifiMb(double wifiMb) {
        return new byte[] { (byte) wifiMb, (byte) (wifiMb % 1D * 0xFF) };
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
