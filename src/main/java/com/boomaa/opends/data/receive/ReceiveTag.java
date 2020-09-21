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
    JOYSTICK_OUTPUT(0x01, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) -> {
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
    }),
    // Indices differ from documentation: Free Space (4, 8) instead of (0,4)
    DISK_INFO(0x04, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("Block", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4))) //inferred
            .addTo("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
    ),
    //TODO field listing does not match data - fix
    CPU_INFO(0x05, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Float>) (packet, size) -> {
        float numCpus = NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4));
        TagValueMap<Float> map = new TagValueMap<Float>().addTo("Number of CPUs", numCpus);
        for (int i = 0; i < numCpus; i++) {
            int n = i + 1;
            int c = i + 4;
            map.addTo("CPU " + n + " Time Critical %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                    .addTo("CPU " + n + " Above Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                    .addTo("CPU " + n + " Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)))
                    .addTo("CPU " + n + " Low %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, c, c += 4)));
        }
        return map;
    }),
    RAM_INFO(0x06, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, DISK_INFO.getAction()),
    PDP_LOG(0x08, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Double>) (packet, size) -> {
        DSLog.PDP_STATS = packet;
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
        // added, not in documentation
        // https://github.com/ligerbots/dslogparser/blob/master/dslogparser/dslogparser.py#L166-L168
        map.addTo("Resistance", (double) packet[packet.length - 3]);
        map.addTo("Voltage", (double) packet[packet.length - 2]);
        map.addTo("Temperature", (double) packet[packet.length - 1]);
        return map;
    }),
    UDP_R2D_UNKNOWN(0x09, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Byte>) TagValueMap::passPackets),
    CAN_METRICS(0x0E, Protocol.UDP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Float>) (packet, size) -> new TagValueMap<Float>()
            .addTo("Utilization %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("Bus Off", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
            .addTo("TX Full", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 8, 12)))
            .addTo("RX Errors", (float) packet[12])
            .addTo("TX Errors", (float) packet[13])
    ),
    RADIO_EVENTS(0x00, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) ->
            TagValueMap.singleton("Message", new String(packet))
    ),
    USAGE_REPORT(0x01, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) UsageReporting::decode),
    DISABLE_FAULTS(0x04, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("Comms", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("12V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
    ),
    RAIL_FAULTS(0x05, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("6V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("5V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
            .addTo("3.3V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6)))
    ),
    VERSION_INFO(0x0A, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) -> {
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
    }),
    ERROR_MESSAGE(0x0B, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) -> {
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
    }),
    STANDARD_OUT(0x0C, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) -> new TagValueMap<String>()
            .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
            .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
            .addTo("Message", new String(ArrayUtils.sliceArr(packet, 6)))
    ),
    TCP_R2D_UNKNOWN(0x0D, Protocol.TCP, Remote.ROBO_RIO, InLog.ALWAYS, (ReceiveTagBase<Byte>) TagValueMap::passPackets),

    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) -> {
        TagValueMap<String> map = new TagValueMap<>();
        String[] nStrs = ArrayUtils.removeBlanks(NumberUtils.extractAllASCII(packet));
        if (nStrs.length >= 2) {
            map.addTo("Status", nStrs[0]).addTo("Version", nStrs[1]);
        }
        return map;
    }),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    DS_VER(0x02, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS, InLog.ALWAYS, WPILIB_VER.action),
    EVENT_CODE(0x14, Protocol.TCP, Remote.FMS, InLog.ALWAYS, (ReceiveTagBase<String>) (packet, size) ->
            TagValueMap.singleton("Event Name", new String(ArrayUtils.sliceArr(packet, 1)))
    ),
    STATION_INFO(0x19, Protocol.TCP, Remote.FMS, InLog.ALWAYS, (ReceiveTagBase<AllianceStation>) (packet, size) -> {
        AllianceStation.Status status = AllianceStation.Status.INVALID;
        switch (packet[1]) {
            case 0: status = AllianceStation.Status.GOOD; break;
            case 1: status = AllianceStation.Status.BAD; break;
            case 2: status = AllianceStation.Status.WAITING; break;
        }
        return TagValueMap.singleton("Alliance Station", AllianceStation.getFromByte(packet[0]).setStatus(status));
    }),
    CHALLENGE_QUESTION(0x1A, Protocol.TCP, Remote.FMS, InLog.ALWAYS, (ReceiveTagBase<Integer>) (packet, size) ->
            TagValueMap.singleton("Challenge Value", NumberUtils.getUInt16(
                    ArrayUtils.sliceArr(packet, packet.length - 2, packet.length)))
    ),
    GAME_DATA(0x1C, Protocol.TCP, Remote.FMS, InLog.ALWAYS, EVENT_CODE.action);

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final InLog includeInLog;
    private final ReceiveTagBase<?> action;

    ReceiveTag(int flag, Protocol protocol, Remote remote, InLog includeInLog, ReceiveTagBase<?> action) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
        this.includeInLog = includeInLog;
        this.action = action;
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

    public ReceiveTagBase<?> getAction() {
        return action;
    }

}
