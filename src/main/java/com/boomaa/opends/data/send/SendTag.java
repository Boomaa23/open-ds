package com.boomaa.opends.data.send;

import com.boomaa.opends.data.UsageReporting;
import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.networking.WlanConnection;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.JoystickType;
import com.boomaa.opends.usb.USBInterface;
import com.boomaa.opends.usb.XboxController;
import com.boomaa.opends.util.NumberUtils;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SendTag {
    COUNTDOWN(0x07, Protocol.UDP, Remote.ROBO_RIO, NullSendTag.get()),
    JOYSTICK(0x0C, Protocol.UDP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        USBInterface.findControllers();
        USBInterface.updateValues();
        Map<Integer, HIDDevice> deviceMap = USBInterface.getControlDevices();
        for (int i = 0; i < HIDDevice.MAX_JS_NUM; i++) {
            HIDDevice ctrl = deviceMap.get(i);
            if (ctrl != null && !ctrl.isDisabled()) {
                builder.addInt(ctrl.numAxes()); //3 axes
                if (ctrl instanceof Joystick) {
                    Joystick js = (Joystick) ctrl;
                    builder.addInt(NumberUtils.dblToInt8(js.getX()))
                            .addInt(NumberUtils.dblToInt8(js.getY()))
                            .addInt(NumberUtils.dblToInt8(js.getZ()));
                } else if (ctrl instanceof XboxController) {
                    XboxController xbox = (XboxController) ctrl;
                    builder.addInt(NumberUtils.dblToInt8(xbox.getX(true)))
                            .addInt(NumberUtils.dblToInt8(xbox.getY(true)))
                            .addInt(NumberUtils.dblToInt8(xbox.getTrigger(true)))
                            .addInt(NumberUtils.dblToInt8(xbox.getTrigger(false)))
                            .addInt(NumberUtils.dblToInt8(xbox.getX(false)))
                            .addInt(NumberUtils.dblToInt8(xbox.getY(false)));
                }
                builder.addInt(ctrl.numButtons())
                        .addBytes(NumberUtils.packBools(ctrl.getButtons()))
                        .addInt(0); //povCount
            } else {
                // Placeholder values for js index padding
                builder.addInt(0).addInt(0).addInt(0); //num axes, btns, povs
            }
        }
        return builder.build();
    }),
    DATE(0x0F, Protocol.UDP, Remote.ROBO_RIO, () -> Date.now().toSendBytes()),
    TIMEZONE(0x10, Protocol.UDP, Remote.ROBO_RIO, () ->
            Calendar.getInstance().getTimeZone().getDisplayName().getBytes()
    ),

    JOYSTICK_DESC(0x02, Protocol.TCP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        USBInterface.findControllers();
        USBInterface.updateValues();
        Map<Integer, HIDDevice> deviceMap = USBInterface.getControlDevices();
        for (int i = 0; i < HIDDevice.MAX_JS_NUM; i++) {
            HIDDevice ctrl = deviceMap.get(i);
            if (ctrl != null && !ctrl.isDisabled()) {
                builder.addInt(ctrl.getIndex())
                        .addInt(ctrl instanceof XboxController ? 1 : 0) //isXbox
                        .addInt((ctrl instanceof XboxController ? JoystickType.XINPUT_GAMEPAD : JoystickType.HID_JOYSTICK).numAsInt());
                //TODO make sure this controller name-getting works VVV
                String name = ctrl.getName();
                builder.addInt(name.length())
                        .addBytes(name.getBytes())
                        .addInt(ctrl.numAxes()); //numAxes
                HIDDevice.Axis[] axes = ctrl instanceof XboxController ? XboxController.Axis.values() : Joystick.Axis.values();
                for (HIDDevice.Axis axis : axes) {
                    builder.addInt(axis.getInt());
                }
                builder.addInt(ctrl.numButtons())
                        .addInt(0); //povCount
            } else {
                builder.addInt(i).pad(0, 6);
            }
        }
        return builder.build();
    }),
    //TODO match info
    MATCH_INFO(0x07, Protocol.TCP, Remote.ROBO_RIO, NullSendTag.get()),
    GAME_DATA(0x0E, Protocol.TCP, Remote.ROBO_RIO, () ->
            MainJDEC.GAME_DATA.getText().getBytes()
    ),

    FIELD_RADIO_METRICS(0x00, Protocol.UDP, Remote.FMS, NullSendTag.get()),
    COMMS_METRICS(0x01, Protocol.UDP, Remote.FMS, NullSendTag.get()),
    LAPTOP_METRICS(0x02, Protocol.UDP, Remote.FMS, () -> {
        PacketBuilder builder = new PacketBuilder().addInt(0x00); //TODO battery percent (not JNI)
        double load = -1;
        int iterations = 0;
        while (load == -1 && ++iterations < 2000) {
            load = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getSystemCpuLoad();
        }
        builder.addInt((int) (load * 100));
        return builder.build();
    }),
    ROBOT_RADIO_METRICS(0x03, Protocol.UDP, Remote.FMS, () ->
            new PacketBuilder().addInt(WlanConnection.getRadio(MainJDEC.TEAM_NUMBER.checkedIntParse()).getSignal())
                    .addInt(0x00).addInt(0x00).build()
            //TODO bandwidth utilization (uint16)
    ),
    PD_INFO(0x04, Protocol.UDP, Remote.FMS, () -> new byte[0]),

    //TODO software versions for FMS
    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    DS_VER(0x02, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    USAGE_REPORT(0x15, Protocol.TCP, Remote.FMS, () -> {
        PacketBuilder builder = new PacketBuilder();
        builder.addBytes(NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse()))
                .addInt(0x00) //Unknown
                .addBytes(UsageReporting.RECEIVED_USAGE);
        return builder.build();
    }),
    LOG_DATA(0x16, Protocol.TCP, Remote.FMS, () -> {
        PacketBuilder builder = new PacketBuilder();
        //TODO implement trip time, lost packets, CAN, signalDb, bandwidth, "Watchdog" on status
        builder.addInt(0x01) //tripTime
               .addInt(0x00); //lostPackets
        builder.addBytes(NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse()));
        int status = 0;
        if (MainJDEC.BROWNOUT_STATUS.isDisplayed()) {
            status += 0x80;
        }
//        if () { //watchdog
//            status += 0x40;
//        }
        RobotMode mode = (RobotMode) MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem();
        if (mode != null) {
            //TODO test if we can add DS and robot status flags
            switch (mode) {
                case TELEOPERATED:
                    status += 0x20 + 0x04;
                    break;
                case AUTONOMOUS:
                    status += 0x10 + 0x02;
                    break;
            }
        }
        if (!MainJDEC.IS_ENABLED.isSelected()) {
            status += 0x08 + 0x01;
        }
        builder.addInt(status)
                .addInt(0x01) //CAN
                .addInt(0x01) //SignalDb
                .addBytes(NumberUtils.intToBytePair(0x01)); //bandwidth
        return builder.build();
    }),
    ERR_AND_EVENT_DATA(0x17, Protocol.TCP, Remote.FMS, NullSendTag.get()),
    TEAM_NUMBER(0x18, Protocol.TCP, Remote.FMS, () ->
            NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse())
    ),
    CHALLENGE_RESPONSE(0x1B, Protocol.TCP, Remote.FMS, () ->
            MainJDEC.CHALLENGE_RESPONSE.getText().getBytes()
    ),
    DS_PING(0x1C, Protocol.TCP, Remote.FMS, () -> new byte[0]);

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final SendTagData[] values;

    SendTag(int flag, Protocol protocol, Remote remote, SendTagData... values) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
        this.values = values;
    }

    public int getFlag() {
        return flag;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Remote getRemote() {
        return remote;
    }

    public SendTagData[] getValues() {
        return values;
    }

    public byte[] getBytes() {
        byte[] tagData = values[MainJDEC.PROTOCOL_YEAR.getSelectedIndex()].getTagData();
        int dataPos = protocol == Protocol.TCP ? 3 : 2;

        byte[] out = new byte[dataPos + tagData.length];
        if (protocol == Protocol.UDP) {
            out[0] = (byte) (tagData.length + 1);
        } else if (protocol == Protocol.TCP) {
            int lenAll = tagData.length + 1;
            byte[] b = NumberUtils.intToBytePair(lenAll);
            out[0] = b[0];
            out[1] = b[1];
        }
        out[dataPos - 1] = (byte) flag;
        System.arraycopy(tagData, 0, out, dataPos, tagData.length);
        return out;
    }

    public static List<SendTag> typeMatches(Protocol protocol, Remote remote) {
        List<SendTag> tags = new ArrayList<>();
        for (SendTag tag : SendTag.values()) {
            if (tag.protocol == protocol && tag.remote == remote) {
                tags.add(tag);
            }
        }
        return tags;
    }
}
