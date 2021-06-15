package com.boomaa.opends.data.receive;

import com.boomaa.opends.data.UsageReporting;
import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.display.InLog;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.DSLog;
import com.boomaa.opends.util.NumberUtils;

public enum ReceiveTag {
    //TODO fix the 2015/2016 CPU, RAM, disk, and CAN usage values. LibDS doesn't look right.
    JOYSTICK_OUTPUT(0x01, Protocol.UDP, Remote.ROBO_RIO, InLog.NEVER,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) -> {
                TagValueMap<String> map = new TagValueMap<>();
                if (size >= 6) {
                    byte[] outBytes = ArrayUtils.sliceArr(packet, 0, 4);
                    StringBuilder sb = new StringBuilder();
                    for (byte b : outBytes) {
                        sb.append(Integer.toBinaryString(b));
                    }
                    map.addTo("Output", sb.toString())
                            .addTo("Left Rumble", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
                            .addTo("Right Rumble", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 6, 8))));
                } else {
                    map.addTo("Output", "none").addTo("Left Rumble", "none").addTo("Right Rumble", "none");
                }
                return map;
            },
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    DISK_INFO(0x04, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Integer>) (packet, size) -> new TagValueMap<Integer>()
                .addTo("Block", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4)))
                .addTo("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8))),
            RefRecieveTag.yearOfAction(2015),
            (ReceiveTagAction<Integer>) (packet, size) ->
                    TagValueMap.singleton("Utilization %", (int) packet[0]),
            NullReceiveTag.getInstance()
    ),
    CPU_INFO(0x05, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Float>) (packet, size) -> {
                float numCpus = packet[0];
                TagValueMap<Float> map = new TagValueMap<Float>().addTo("Number of CPUs", numCpus);
                int c = 1;
                for (int n = 0; n < numCpus; n++) {
                    map.addTo("CPU " + n + " Time Critical %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                            .addTo("CPU " + n + " Above Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                            .addTo("CPU " + n + " Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                            .addTo("CPU " + n + " Low %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)));
                }
                return map;
            },
            RefRecieveTag.yearOfAction(2015),
            (ReceiveTagAction<Integer>) (packet, size) ->
                    TagValueMap.singleton("Utilization %", (int) packet[0]),
            NullReceiveTag.getInstance()
    ),
    RAM_INFO(0x06, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, DISK_INFO.getActions()),
    //TODO fix this, not correct
    // are channels ordered backwards? https://www.chiefdelphi.com/t/alternate-viewer-for-driver-station-logs-dslog/120629/13?u=boomaa
    PDP_LOG(0x08, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Double>) (packet, size) -> {
                DSLog.PDP_STATS = ArrayUtils.sliceArr(packet, 1);
                TagValueMap<Double> map = new TagValueMap<>();
                StringBuilder binaryBuilder = new StringBuilder();
                for (int i = 1; i < packet.length - 3; i++) {
                    binaryBuilder.append(NumberUtils.padByte(packet[i]));
                }
                char[] binaryChars = binaryBuilder.toString().toCharArray();
                int[] binary = new int[binaryChars.length];
                for (int i = 0; i < binary.length; i++) {
                    binary[i] = Character.getNumericValue(binaryChars[i]);
                }
                int pdpNum = 0;
                double totalCurrent = 0;
                for (int bitCtr = 0; bitCtr <= binary.length - 10; bitCtr += 10) {
                    double portCurrent = NumberUtils.getUInt10(ArrayUtils.sliceArr(binary, bitCtr, bitCtr + 10)) / 8.0;
                    totalCurrent += portCurrent;
                    map.addTo("Port " + ((pdpNum < 10) ? "0" : "") + pdpNum + " Current", portCurrent);
                    bitCtr += (++pdpNum == 6 || pdpNum == 12) ? 4 : 0;
                }
                map.addTo("Total Current", totalCurrent);
                // added, not in FRCture documentation
                // https://github.com/ligerbots/dslogparser/blob/master/dslogparser/dslogparser.py#L166-L168
                map.addTo("Resistance", (double) packet[packet.length - 3]);
                map.addTo("Voltage", (double) packet[packet.length - 2]);
                map.addTo("Temperature", (double) packet[packet.length - 1]);
                return map;
            },
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    UDP_R2D_UNKNOWN(0x09, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Byte>) TagValueMap::passPackets,
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    CAN_METRICS(0x0E, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Float>) (packet, size) -> new TagValueMap<Float>()
                .addTo("Utilization %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4)))
                .addTo("Bus Off", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
                .addTo("TX Full", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 8, 12)))
                .addTo("RX Errors", (float) packet[12])
                .addTo("TX Errors", (float) packet[13]),
            RefRecieveTag.yearOfAction(2015),
            (ReceiveTagAction<Integer>) (packet, size) ->
                    TagValueMap.singleton("Utilization %", (int) packet[0]),
            NullReceiveTag.getInstance()
    ),
    RADIO_EVENTS(0x00, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) ->
                TagValueMap.singleton("Message", new String(packet)),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    USAGE_REPORT(0x01, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) UsageReporting::decode,
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    //TODO fix disable and rail faults (record more data?)
    DISABLE_FAULTS(0x04, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Integer>) (packet, size) -> new TagValueMap<Integer>()
                .addTo("Comms", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
                .addTo("12V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4))),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    RAIL_FAULTS(0x05, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Integer>) (packet, size) -> new TagValueMap<Integer>(),
//                .addTo("6V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
//                .addTo("5V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
//                .addTo("3.3V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    VERSION_INFO(0x0A, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) -> {
                TagValueMap<String> map = new TagValueMap<>();
                String devType = "Unknown";
                switch (packet[0]) {
                    case 0: devType = "Software"; break;
                    case 2: devType = "CAN Talon"; break;
                    case 8: devType = "PDP"; break;
                    case 9: devType = "PCM"; break;
                    case 21: devType = "Pigeon"; break;
                }
                map.put("Device Type", devType);
                map.put("ID", String.valueOf(packet[3]));
                String[] nameAndVer = NumberUtils.getNLengthStrs(ArrayUtils.sliceArr(packet, 4), 1, true);
                map.put("Name", nameAndVer[0]);
                map.put("Version", nameAndVer[1]);
                return map;
            },
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    ERROR_MESSAGE(0x0B, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) -> {
                TagValueMap<String> map = new TagValueMap<String>()
                        .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
                        .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
                        .addTo("Error Code", String.valueOf(NumberUtils.getInt32(ArrayUtils.sliceArr(packet, 8, 12))));
                if (NumberUtils.hasPlacedBit(packet[12], 7)) {
                    map.addTo("Flag", "Error");
                } else if (NumberUtils.hasPlacedBit(packet[12], 6)) {
                    map.addTo("Flag", "isLVcode");
                }
                String[] detLocCall = NumberUtils.getNLengthStrs(ArrayUtils.sliceArr(packet, 13), 2, true);
                if (detLocCall.length >= 2) {
                    map.addTo("Details", detLocCall[0]);
                    map.addTo("Location", detLocCall[1]);
                    map.addTo("Call Stack", detLocCall[2]);
                }
                return map;
            },
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    STANDARD_OUT(0x0C, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) -> new TagValueMap<String>()
                .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
                .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
                .addTo("Message", new String(ArrayUtils.sliceArr(packet, 6))),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    TCP_R2D_UNKNOWN(0x0D, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Byte>) TagValueMap::passPackets,
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),

    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) -> {
                TagValueMap<String> map = new TagValueMap<>();
                String[] nStrs = ArrayUtils.removeBlanks(NumberUtils.extractAllASCII(packet));
                if (nStrs.length >= 2) {
                    map.addTo("Status", nStrs[0]).addTo("Version", nStrs[1]);
                }
                return map;
            },
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    DS_VER(0x02, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.actions),
    EVENT_CODE(0x14, Protocol.TCP, Remote.FMS, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<String>) (packet, size) ->
                TagValueMap.singleton("Event Name", new String(ArrayUtils.sliceArr(packet, 1))),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    STATION_INFO(0x19, Protocol.TCP, Remote.FMS, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<AllianceStation>) (packet, size) ->
                TagValueMap.singleton("Alliance Station", AllianceStation.getFromByte(packet[0])
                    .setStatus(packet[1] <= 2 ? AllianceStation.Status.values()[packet[1]] : AllianceStation.Status.INVALID)),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    CHALLENGE_QUESTION(0x1A, Protocol.TCP, Remote.FMS, InLog.ALWAYS,
            RefRecieveTag.yearOfAction(2020),
            (ReceiveTagAction<Integer>) (packet, size) ->
                TagValueMap.singleton("Challenge Value", NumberUtils.getUInt16(
                    ArrayUtils.sliceArr(packet, packet.length - 2, packet.length))),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance(),
            NullReceiveTag.getInstance()
    ),
    GAME_DATA(0x1C, Protocol.TCP, Remote.FMS, InLog.ALWAYS, EVENT_CODE.actions);

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final InLog includeInLog;
    private final ReceiveTagAction<?>[] actions;

    ReceiveTag(int flag, Protocol protocol, Remote remote, InLog includeInLog, ReceiveTagAction<?>... actions) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
        this.includeInLog = includeInLog;
        this.actions = actions;
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

    public boolean includeInLog() {
        return includeInLog.isInLog();
    }

    public ReceiveTagAction<?>[] getActions() {
        return actions;
    }
}
