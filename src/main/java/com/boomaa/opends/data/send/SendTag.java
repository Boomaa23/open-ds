package com.boomaa.opends.data.send;

import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.JoystickType;
import com.boomaa.opends.usb.USBInterface;
import com.boomaa.opends.util.NumberUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public enum SendTag {
    COUNTDOWN(0x07, Protocol.UDP, Remote.ROBO_RIO, null),
    JOYSTICK(0x0C, Protocol.UDP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        for (Joystick js : USBInterface.getJoysticks()) {
            builder.addInt(js.numAxes()); //3 axes
            builder.addInt(NumberUtils.dblToInt8(js.getX()));
            builder.addInt(NumberUtils.dblToInt8(js.getY()));
            builder.addInt(NumberUtils.dblToInt8(js.getZ()));
            builder.addInt(js.numButtons());
            builder.addBytes(NumberUtils.packBools(js.getButtons()));
            builder.addInt(0); //povCount
        }
        return builder.build();
    }),
    DATE(0x0F, Protocol.UDP, Remote.ROBO_RIO, () -> Date.now().toSendBytes()),
    TIMEZONE(0x10, Protocol.UDP, Remote.ROBO_RIO, () -> Calendar.getInstance().getTimeZone().getDisplayName().getBytes()),

    JOYSTICK_DESC(0x02, Protocol.TCP, Remote.ROBO_RIO, () -> {
        PacketBuilder builder = new PacketBuilder();
        List<Joystick> js = USBInterface.getJoysticks();
        for (int i = 0; i < js.size() && i < Joystick.MAX_JS_NUM; i++) {
            builder.addInt(i);
            builder.addInt(0); //isXbox
            builder.addInt(JoystickType.HID_JOYSTICK.numAsInt());
            //TODO make sure this controller name-getting works VVV
            builder.addBytes(js.get(i).getController().getName().getBytes());
            builder.addInt(js.get(i).numAxes()); //numAxes
            builder.addInt(JoystickType.Axis.X.getInt());
            builder.addInt(JoystickType.Axis.Y.getInt());
            builder.addInt(JoystickType.Axis.Z.getInt());
            builder.addInt(js.get(i).numButtons());
            builder.addInt(0); //povCount
        }
        return builder.build();
    }),
    MATCH_INFO(0x07, Protocol.TCP, Remote.ROBO_RIO, null),
    GAME_DATA(0x0E, Protocol.TCP, Remote.ROBO_RIO, () -> MainJDEC.GAME_DATA.getText().getBytes()),

    FIELD_RADIO_METRICS(0x00, Protocol.UDP, Remote.FMS, null),
    COMMS_METRICS(0x01, Protocol.UDP, Remote.FMS, null),
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
    USAGE_REPORT(0x15, Protocol.TCP, Remote.FMS, null),
    LOG_DATA(0x16, Protocol.TCP, Remote.FMS, null),
    ERR_AND_EVENT_DATA(0x17, Protocol.TCP, Remote.FMS, null),
    TEAM_NUMBER(0x18, Protocol.TCP, Remote.FMS, null),
    CHALLENGE_RESPONSE(0x1B, Protocol.TCP, Remote.FMS, null),
    DS_PING(0x1C, Protocol.TCP, Remote.FMS, null);

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
            out[0] = (byte) (lenAll & 0xFF);
            out[1] = (byte) ((lenAll >>> 8) & 0xFF);
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
