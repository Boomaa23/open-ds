package com.boomaa.opends.data.send;

import com.boomaa.opends.data.UsageReporting;
import com.boomaa.opends.data.holders.Date;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.networking.WlanConnection;
import com.boomaa.opends.usb.Component;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.usb.Controller;
import com.boomaa.opends.usb.HIDDevice;
import com.boomaa.opends.util.NumberUtils;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public enum SendTag {
    COUNTDOWN(0x07, Protocol.UDP, Remote.ROBO_RIO,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    JOYSTICK(0x0C, Protocol.UDP, Remote.ROBO_RIO,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> {
                PacketBuilder builder = new PacketBuilder();
                HIDDevice dev = ControlDevices.getAll().get(ControlDevices.iterateSend(true));
                if (dev != null && !dev.isDisabled()) {
                    dev.update();
                    builder.addInt(HIDDevice.DEFAULT_AXIS_MAX); // numAxes
                    Map<Component.Identifier, Integer> axesMap = dev.getAxesTracker().getDirectMap();
                    for (int i = 0; i < HIDDevice.DEFAULT_AXIS_MAX; i++) {
                        Component.Identifier id = Component.Axis.values()[i];
                        if (axesMap.containsKey(id)) {
                            Component comp = dev.getComponent(axesMap.get(id));
                            builder.addInt(NumberUtils.dblToInt8(comp.getValue()));
                        } else {
                            builder.addInt(0);
                        }
                    }
                    builder.addInt(dev.usedNumButtons())
                            .addBytes(NumberUtils.packBools(dev.getButtons()))
                            .addInt(0); //povCount
                } else {
                    // Placeholder values for js index padding
                    builder.addInt(0).addInt(0).addInt(0); //num axes, btns, povs
                }
                return builder.build();
            },
            RefSendTag.yearOfAction(2015),
            RefSendTag.yearOfAction(2020),
            NullSendTag.getInstance()
    ),
    DATE(0x0F, Protocol.UDP, Remote.ROBO_RIO,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> Date.now().toSendBytes(),
            RefSendTag.yearOfAction(2015),
            RefSendTag.yearOfAction(2020),
            NullSendTag.getInstance()
    ),
    TIMEZONE(0x10, Protocol.UDP, Remote.ROBO_RIO,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> Calendar.getInstance().getTimeZone().getDisplayName().getBytes(),
            RefSendTag.yearOfAction(2015),
            RefSendTag.yearOfAction(2020),
            NullSendTag.getInstance()
    ),

    JOYSTICK_DESC(0x02, Protocol.TCP, Remote.ROBO_RIO,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> {
                PacketBuilder builder = new PacketBuilder();
                int idx = ControlDevices.iterateSend(false);
                HIDDevice dev = ControlDevices.getAll().get(idx);
                if (dev != null && !dev.isDisabled()) {
                    builder.addInt(dev.getIdx())
                            .addInt(dev.getDeviceType() == Controller.Type.HID_GAMEPAD ? 1 : 0) //isXbox
                            .addInt(dev.getDeviceType().getFRCFlag());
                    String name = dev.getName();
                    builder.addInt(name.length())
                            .addBytes(name.getBytes())
                            .addInt(HIDDevice.DEFAULT_AXIS_MAX); //numAxes
                    for (int i = 0; i < HIDDevice.DEFAULT_AXIS_MAX; i++) {
                        builder.addInt(i % 3); //axesTypes
                    }
                    builder.addInt(dev.usedNumButtons())
                            .addInt(0); //povCount
                } else {
                    builder.addInt(idx)
                            .addInt(0) //isXbox
                            .addInt(Controller.Type.UNKNOWN.getFRCFlag())
                            .pad(0, 4);
                }
                return builder.build();
            },
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    //TODO match info
    MATCH_INFO(0x07, Protocol.TCP, Remote.ROBO_RIO,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    GAME_DATA(0x0E, Protocol.TCP, Remote.ROBO_RIO,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> MainJDEC.GAME_DATA.getText().getBytes(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),

    FIELD_RADIO_METRICS(0x00, Protocol.UDP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    COMMS_METRICS(0x01, Protocol.UDP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    LAPTOP_METRICS(0x02, Protocol.UDP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> {
                PacketBuilder builder = new PacketBuilder().addInt(0x00); //TODO battery percent (not JNI)
                double load = -1;
                int iterations = 0;
                while (load == -1 && ++iterations < 2000) {
                    load = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class).getSystemCpuLoad();
                }
                builder.addInt((int) (load * 100));
                return builder.build();
            },
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    ROBOT_RADIO_METRICS(0x03, Protocol.UDP, Remote.FMS,
            //TODO bandwidth utilization (uint16)
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> new PacketBuilder().addInt(WlanConnection.getRadio().getSignal())
                    .addInt(0x00).addInt(0x00).build(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    PD_INFO(0x04, Protocol.UDP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> new byte[0],
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),

    //TODO software versions for FMS
    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    DS_VER(0x02, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    USAGE_REPORT(0x15, Protocol.TCP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> {
                PacketBuilder builder = new PacketBuilder();
                builder.addBytes(NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse()))
                        .addInt(0x00) //Unknown
                        .addBytes(UsageReporting.RECEIVED_USAGE);
                return builder.build();
            },
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    LOG_DATA(0x16, Protocol.TCP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> {
                PacketBuilder builder = new PacketBuilder();
                //TODO implement trip time, lost packets, CAN, signalDb, bandwidth, "Watchdog" on status
                builder.addInt(0x01) //tripTime
                       .addInt(0x00); //lostPackets
                builder.addBytes(NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse()));
                int status = 0;
                if (MainJDEC.ESTOP_STATUS.isDisplayed()) {
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
            },
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    ERR_AND_EVENT_DATA(0x17, Protocol.TCP, Remote.FMS,
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    TEAM_NUMBER(0x18, Protocol.TCP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> NumberUtils.intToBytePair(MainJDEC.TEAM_NUMBER.checkedIntParse()),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    CHALLENGE_RESPONSE(0x1B, Protocol.TCP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () ->  MainJDEC.CHALLENGE_RESPONSE.getText().getBytes(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    ),
    DS_PING(0x1C, Protocol.TCP, Remote.FMS,
            RefSendTag.yearOfAction(2020),
            RefSendTag.yearOfAction(2020),
            () -> new byte[0],
            NullSendTag.getInstance(),
            NullSendTag.getInstance(),
            NullSendTag.getInstance()
    );

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final SendTagData[] values;

    SendTag(int flag, Protocol protocol, Remote remote, SendTagData... values) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
        this.values = values;
        if (values.length != DisplayEndpoint.VALID_PROTOCOL_YEARS.length) {
            throw new IllegalArgumentException("Send tag " + name() + " has a mismatched number of year-values");
        }
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
        SendTagData data = values[MainJDEC.getProtocolIndex()];
        if (data instanceof RefSendTag) {
            data = values[((RefSendTag) data).getIndex()];
        }
        byte[] tagData = data.getTagData();
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
