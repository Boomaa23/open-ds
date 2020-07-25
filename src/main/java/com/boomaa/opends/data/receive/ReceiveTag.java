package com.boomaa.opends.data.receive;

import com.boomaa.opends.data.UsageReporting;
import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public enum ReceiveTag {
    //TODO joystick output parsing
    JOYSTICK_OUTPUT(0x01, Protocol.UDP, Remote.ROBO_RIO, null),
    DISK_INFO(0x04, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Long>) (packet, size) ->
            TagValueMap.singleton("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4)))
    ),
    CPU_INFO(0x05, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Float>) (packet, size) -> new TagValueMap<Float>()
            .addTo("Number of CPUs", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("CPU 1 Time Critical %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 4, 8)))
            .addTo("CPU 1 Above Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 8, 12)))
            .addTo("CPU 1 Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 12, 16)))
            .addTo("CPU 1 Low %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 16, 20)))
            .addTo("CPU 2 Time Critical %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 20, 24)))
            .addTo("CPU 2 Above Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 24, 28)))
            .addTo("CPU 2 Normal %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 28, 32)))
            .addTo("CPU 2 Low %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 32, 36)))
    ),
    RAM_INFO(0x06, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Long>) (packet, size) -> new TagValueMap<Long>()
            .addTo("Block", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
    ),
    PDP_LOG(0x08, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Integer>) (packet, size) -> {
        int[] rawValues = new int[packet.length * 8];
        for (int i = 0; i < packet.length; i++) {
            char[] bin = NumberUtils.padByte(packet[i]).toCharArray();
            for (int j = 0; j < 8; j++) {
                rawValues[(i * 8) + j] = Character.getNumericValue(bin[j]);
            }
        }
        TagValueMap<Integer> map = new TagValueMap<>();
        int ctr = 0;
        for (int i = 0; i < 15; i++) {
            ctr += (i == 5 || i == 10) ? 14 : 10;
            map.addTo("PDP Port " + (i < 10 ? "0" : "") + i, NumberUtils.getUInt16(ArrayUtils.sliceArr(rawValues, ctr, ctr + 10)));
        }
        return map;
    }),
    UDP_R2D_UNKNOWN(0x09, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Byte>) TagValueMap::passPackets),
    CAN_METRICS(0x0E, Protocol.UDP, Remote.ROBO_RIO, (ReceiveTagBase<Float>) (packet, size) -> new TagValueMap<Float>()
            .addTo("Utilization %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("Bus Off", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
            .addTo("TX Full", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 8, 12)))
            .addTo("RX Errors", (float) NumberUtils.getUInt8(packet[12]))
            .addTo("TX Errors", (float) NumberUtils.getUInt8(packet[13]))
    ),
    RADIO_EVENTS(0x00, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<String>) (packet, size) ->
            TagValueMap.singleton("Message", new String(packet))
    ),
    USAGE_REPORT(0x01, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<String>) UsageReporting::build),
    DISABLE_FAULTS(0x04, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("Comms", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("12V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
    ),
    RAIL_FAULTS(0x05, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("6V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("5V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
            .addTo("3.3V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6)))
    ),
    VERSION_INFO(0x0A, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<String>) (packet, size) -> {
        TagValueMap<String> map = new TagValueMap<>();
        String devType = null;
        switch (NumberUtils.getUInt8(packet[0])) {
            case 0: devType = "Software"; break;
            case 2: devType = "CAN Talon"; break;
            case 8: devType = "PDP"; break;
            case 9: devType = "PCM"; break;
        }
        map.put("Device Type", devType);
        map.put("ID", String.valueOf(NumberUtils.getUInt8(packet[3])));
        //TODO implement name and ver to ver info
        // name (str, 1+n)
        // ver (str, 1+n)
        return map;
    }),
    ERROR_MESSAGE(0x0B, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<String>) (packet, size) -> {
        TagValueMap<String> map = new TagValueMap<String>()
                .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
                .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
                .addTo("Error Code", String.valueOf(NumberUtils.getInt32(ArrayUtils.sliceArr(packet, 8, 12))))
                .addTo("Message", new String(ArrayUtils.sliceArr(packet, 15)));
        if (NumberUtils.hasPlacedBit(packet[12], 7)) {
            map.addTo("Flag", "Error");
        } else if (NumberUtils.hasPlacedBit(packet[12], 6)) {
            map.addTo("Flag", "isLVcode");
        }
        return map;
        //TODO implement details, location, callstack to err msg
        // details (str, 2+n)
        // location (str, 2+n)
        // callstack (str, 2+n)
    }),
    STANDARD_OUT(0x0C, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<String>) (packet, size) -> new TagValueMap<String>()
            .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
            .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
            .addTo("Message", new String(ArrayUtils.sliceArr(packet, 6)))
    ),
    TCP_R2D_UNKNOWN(0x0D, Protocol.TCP, Remote.ROBO_RIO, (ReceiveTagBase<Byte>) TagValueMap::passPackets),

    WPILIB_VER(0x00, Protocol.TCP, Remote.FMS, null),
    RIO_VER(0x01, Protocol.TCP, Remote.FMS, null),
    DS_VER(0x02, Protocol.TCP, Remote.FMS, null),
    PDP_VER(0x03, Protocol.TCP, Remote.FMS, null),
    PCM_VER(0x04, Protocol.TCP, Remote.FMS, null),
    CANJAG_VER(0x05, Protocol.TCP, Remote.FMS, null),
    CANTALON_VER(0x06, Protocol.TCP, Remote.FMS, null),
    THIRD_PARTY_DEVICE_VER(0x07, Protocol.TCP, Remote.FMS, null),
    EVENT_CODE(0x14, Protocol.TCP, Remote.FMS, (ReceiveTagBase<String>) (packet, size) ->
            TagValueMap.singleton("Event Name", new String(packet))
    ),
    STATION_INFO(0x19, Protocol.TCP, Remote.FMS, (ReceiveTagBase<AllianceStation>) (packet, size) -> {
        String status = "";
        switch (NumberUtils.getUInt8(packet[1])) {
            case 0: status = "Good"; break;
            case 1: status = "Bad"; break;
            case 2: status = "Waiting"; break;
        }
        return TagValueMap.singleton("Alliance Station", AllianceStation.getFromByte(packet[0]).setStatus(status));
    }),
    CHALLENGE_QUESTION(0x1A, Protocol.TCP, Remote.FMS, (ReceiveTagBase<Integer>) (packet, size) ->
            TagValueMap.singleton("Challenge Value", NumberUtils.getUInt16(
                    ArrayUtils.sliceArr(packet, packet.length - 2, packet.length)))
    ),
    GAME_DATA(0x1C, Protocol.TCP, Remote.FMS, (ReceiveTagBase<String>) (packet, size) ->
            TagValueMap.singleton("Game Data", new String(packet))
    );

    private final int flag;
    private final Protocol protocol;
    private final Remote remote;
    private final ReceiveTagBase<?> action;

    ReceiveTag(int flag, Protocol protocol, Remote remote, ReceiveTagBase<?> action) {
        this.flag = flag;
        this.protocol = protocol;
        this.remote = remote;
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

    public ReceiveTagBase<?> getAction() {
        return action;
    }
}