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

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public enum SendTag {
    COUNTDOWN(0x07, Protocol.UDP, Remote.ROBO_RIO, null),
    JOYSTICK(0x0C, Protocol.UDP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        for (HIDDevice ctrl : USBInterface.getControlDevices()) {
            builder.addInt(ctrl.numAxes()); //3 axes
            if (ctrl instanceof Joystick) {
                Joystick js = (Joystick) ctrl;
                builder.addInt(NumberUtils.dblToInt8(js.getX()));
                builder.addInt(NumberUtils.dblToInt8(js.getY()));
                builder.addInt(NumberUtils.dblToInt8(js.getZ()));
            } else if (ctrl instanceof XboxController) {
                XboxController xbox = (XboxController) ctrl;
                builder.addInt(NumberUtils.dblToInt8(xbox.getX(true)));
                builder.addInt(NumberUtils.dblToInt8(xbox.getY(true)));
                builder.addInt(NumberUtils.dblToInt8(xbox.getTrigger(true)));
                builder.addInt(NumberUtils.dblToInt8(xbox.getTrigger(false)));
                builder.addInt(NumberUtils.dblToInt8(xbox.getX(false)));
                builder.addInt(NumberUtils.dblToInt8(xbox.getY(false)));
            }
            builder.addInt(ctrl.numButtons());
            builder.addBytes(NumberUtils.packBools(ctrl.getButtons()));
            builder.addInt(0); //povCount
        }
        return builder.build();
    }),
    DATE(0x0F, Protocol.UDP, Remote.ROBO_RIO, () -> Date.now().toSendBytes()),
    TIMEZONE(0x10, Protocol.UDP, Remote.ROBO_RIO, () ->
            Calendar.getInstance().getTimeZone().getDisplayName().getBytes()
    ),

    JOYSTICK_DESC(0x02, Protocol.TCP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        List<HIDDevice> ctrl = USBInterface.getControlDevices();
        for (int i = 0; i < ctrl.size() && i < HIDDevice.MAX_JS_NUM; i++) {
            HIDDevice cDev = ctrl.get(i);
            builder.addInt(i);
            builder.addInt(cDev instanceof XboxController ? 1 : 0); //isXbox
            builder.addInt((cDev instanceof XboxController ? JoystickType.XINPUT_GAMEPAD : JoystickType.HID_JOYSTICK).numAsInt());
            //TODO make sure this controller name-getting works VVV
            String name = cDev.getController().getName();
            builder.addInt(name.length());
            builder.addBytes(name.getBytes());
            builder.addInt(cDev.numAxes()); //numAxes
            HIDDevice.Axis[] axes = cDev instanceof XboxController ? XboxController.Axis.values() : Joystick.Axis.values();
            for (HIDDevice.Axis axis : axes) {
                builder.addInt(axis.getInt());
            }
            builder.addInt(cDev.numButtons());
            builder.addInt(0); //povCount
        }
        return builder.build();
    }),
    MATCH_INFO(0x07, Protocol.TCP, Remote.ROBO_RIO, null),
    GAME_DATA(0x0E, Protocol.TCP, Remote.ROBO_RIO, () ->
            MainJDEC.GAME_DATA.getText().getBytes()
    ),

    FIELD_RADIO_METRICS(0x00, Protocol.UDP, Remote.FMS, () ->
            new PacketBuilder().addInt(WlanConnection.getRadio(Integer.parseInt(MainJDEC.TEAM_NUMBER.getText())).getSignal())
                    .addInt(0x00).addInt(0x00).build() //TODO bandwidth utilization (uint16)
    ),
    //TODO lost packets (uint16), sent packets (uint16), avg trip time (uint8)
    COMMS_METRICS(0x01, Protocol.UDP, Remote.FMS, () ->
            new PacketBuilder().addInt(0x00).addInt(0x00)
                    .addInt(0x00).addInt(0x00)
                    .addInt(0x00).build()
    ),
    LAPTOP_METRICS(0x02, Protocol.UDP, Remote.FMS, null),
    ROBOT_RADIO_METRICS(0x03, Protocol.UDP, Remote.FMS, null),
    PD_INFO(0x04, Protocol.UDP, Remote.FMS, null),

    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS, null),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS, null),
    DS_VER(0x02, Protocol.TCP, Remote.FMS, null),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS, null),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS, null),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS, null),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS, null),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS, null),
    USAGE_REPORT(0x15, Protocol.TCP, Remote.FMS, () -> {
        PacketBuilder builder = new PacketBuilder();
        builder.addBytes(NumberUtils.intToBytePair(Integer.parseInt(MainJDEC.TEAM_NUMBER.getText())));
        builder.addInt(0x00); //Unknown
        //TODO add usage report encoding
        builder.addBytes(UsageReporting.encode(new String[0], new String[9], new UsageReporting.IdPrefix[0]));
        return builder.build();
    }),
    LOG_DATA(0x16, Protocol.TCP, Remote.FMS, () -> {
        PacketBuilder builder = new PacketBuilder();
        //TODO implement trip time, lost packets, CAN, signalDb, bandwidth, "Watchdog" on status
        builder.addInt(0x01); //tripTime
        builder.addInt(0x00); //lostPackets
        builder.addBytes(NumberUtils.intToBytePair(Integer.parseInt(MainJDEC.TEAM_NUMBER.getText())));
        int status = 0;
        if (MainJDEC.BROWNOUT_STATUS.isDisplayed()) {
            status += 0x80;
        }
//        if () {
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
        builder.addInt(status);
        builder.addInt(0x01); //CAN
        builder.addInt(0x01); //SignalDb
        builder.addBytes(NumberUtils.intToBytePair(0x01)); //bandwidth
        return builder.build();
    }),
    ERR_AND_EVENT_DATA(0x17, Protocol.TCP, Remote.FMS, null),
    TEAM_NUMBER(0x18, Protocol.TCP, Remote.FMS, () ->
            NumberUtils.intToBytePair(Integer.parseInt(MainJDEC.TEAM_NUMBER.getText()))
    ),
    CHALLENGE_RESPONSE(0x1B, Protocol.TCP, Remote.FMS, () ->
            MainJDEC.CHALLENGE_RESPONSE.getText().getBytes()
    ),
    DS_PING(0x1C, Protocol.TCP, Remote.FMS, () -> new byte[0]);

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final SendTagBase value;

    SendTag(int flag, Protocol protocol, Remote remote, SendTagBase value) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
        this.value = value;
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

    public SendTagBase getValue() {
        return value;
    }

    public byte[] getBytes() {
        byte[] tagData = value.getTagData();
        int dataPos = protocol == Protocol.TCP ? 3 : 2;

        byte[] out = new byte[dataPos + tagData.length];
        if (protocol == Protocol.UDP) {
            out[0] = (byte) (tagData.length + 1);
        } else if (protocol == Protocol.TCP) {
            int lenAll = tagData.length + 1;
            byte[] b = NumberUtils.intToBytePair(lenAll);
            //TODO replace with arraycopy()
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
