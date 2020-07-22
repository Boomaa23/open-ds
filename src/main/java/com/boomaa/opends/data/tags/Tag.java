package com.boomaa.opends.data.tags;

import com.boomaa.opends.data.UsageReporting;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public enum Tag {
    //TODO joystick output parsing
    JOYSTICK_OUTPUT(0x01, Protocol.UDP, Source.ROBO_RIO, null),
    DISK_INFO(0x04, Protocol.UDP, Source.ROBO_RIO, (TagBase<Long>) (packet, size) ->
            TagValueMap.singleton("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4)))
    ),
    CPU_INFO(0x05, Protocol.UDP, Source.ROBO_RIO, (TagBase<Float>) (packet, size) -> new TagValueMap<Float>()
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
    RAM_INFO(0x06, Protocol.UDP, Source.ROBO_RIO, (TagBase<Long>) (packet, size) -> new TagValueMap<Long>()
            .addTo("Block", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("Free Space", NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
    ),
    PDP_LOG(0x08, Protocol.UDP, Source.ROBO_RIO, (TagBase<Integer>) (packet, size) -> {
        int[] rawValues = new int[packet.length * 8];
        for (int i = 0; i < packet.length; i++) {
            char[] bin = Integer.toBinaryString(packet[i]).toCharArray();
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
    UDP_R2D_UNKNOWN(0x09, Protocol.UDP, Source.ROBO_RIO, (TagBase<Byte>) TagValueMap::passPackets),
    CAN_METRICS(0x0E, Protocol.UDP, Source.ROBO_RIO, (TagBase<Float>) (packet, size) -> new TagValueMap<Float>()
            .addTo("Utilization %", NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0, 4)))
            .addTo("Bus Off", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 4, 8)))
            .addTo("TX Full", (float) NumberUtils.getUInt32(ArrayUtils.sliceArr(packet, 8, 12)))
            .addTo("RX Errors", (float) NumberUtils.getUInt8(packet[12]))
            .addTo("TX Errors", (float) NumberUtils.getUInt8(packet[13]))
    ),
    RADIO_EVENTS(0x00, Protocol.TCP, Source.ROBO_RIO, (TagBase<String>) (packet, size) ->
            TagValueMap.singleton("Message", new String(packet))
    ),
    USAGE_REPORT(0x01, Protocol.TCP, Source.ROBO_RIO, (TagBase<String>) UsageReporting::build),
    DISABLE_FAULTS(0x04, Protocol.TCP, Source.ROBO_RIO, (TagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("Comms", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("12V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
    ),
    RAIL_FAULTS(0x05, Protocol.TCP, Source.ROBO_RIO, (TagBase<Integer>) (packet, size) -> new TagValueMap<Integer>()
            .addTo("6V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 0, 2)))
            .addTo("5V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 2, 4)))
            .addTo("3.3V", NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6)))
    ),
    //TODO implement ver info parsing
    VERSION_INFO(0x0A, Protocol.TCP, Source.ROBO_RIO, null),
    ERROR_MESSAGE(0x0B, Protocol.TCP, Source.ROBO_RIO, (TagBase<String>) (packet, size) -> new TagValueMap<String>()
            .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
            .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
            .addTo("Error Code", String.valueOf(NumberUtils.getInt32(ArrayUtils.sliceArr(packet, 6, 10))))
            .addTo("Flag", packet[10] == 0x02 ? "isLVcode" : packet[10] == 0x01 ? "Error" : "None")
            //TODO implement error message, std output parsing (+ n-length)
    ),
    STANDARD_OUT(0x0C, Protocol.TCP, Source.ROBO_RIO, (TagBase<String>) (packet, size) -> new TagValueMap<String>()
            .addTo("Timestamp", String.valueOf(NumberUtils.getFloat(ArrayUtils.sliceArr(packet, 0 ,4))))
            .addTo("Sequence Num", String.valueOf(NumberUtils.getUInt16(ArrayUtils.sliceArr(packet, 4, 6))))
            .addTo("Message", new String(ArrayUtils.sliceArr(packet, 6)))
    ),
    TCP_R2D_UNKNOWN(0x0D, Protocol.TCP, Source.ROBO_RIO, (TagBase<Byte>) TagValueMap::passPackets)
    ;

    private final int flag;
    private final Protocol protocol;
    private final Source source;
    private final TagBase<?> action;

    Tag(int flag, Protocol protocol, Source source, TagBase<?> action) {
        this.flag = flag;
        this.protocol = protocol;
        this.source = source;
        this.action = action;
    }

    public int getFlag() {
        return flag;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Source getSource() {
        return source;
    }

    public TagBase<?> getAction() {
        return action;
    }

    public enum Source {
        ROBO_RIO, FMS
    }

    public enum Protocol {
        UDP, TCP
    }
}