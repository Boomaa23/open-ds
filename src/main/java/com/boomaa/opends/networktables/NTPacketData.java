package com.boomaa.opends.networktables;

import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public class NTPacketData {
    private final byte[] data;
    private final NTMessageType messageType;
    private int msgId = -1;
    private int seqNum = -1;
    private int flags = -1; //TODO add flags
    private NTDataType dataType;
    private String msgStr = "null";
    private Object value = "null";
    private int usedLength = 0;

    public NTPacketData(byte[] data) {
        if (data.length < 2) {
            throw new IllegalArgumentException("Packet passed is too short");
        }
        this.data = data;
        this.messageType = NTMessageType.getFromFlag(data[0]);
        this.usedLength++;
        switch (messageType) {
            case kEntryAssign:
                this.msgStr = readString(1);
                this.dataType = NTDataType.getFromFlag(data[usedLength++]);
                this.msgId = extractUInt16(usedLength);
                this.seqNum = extractUInt16(usedLength);
                this.value = extractValue(usedLength);
                //TODO differentiate path from name, add tables and nesting
                NTStorage.ENTRIES.put(msgId, new NTEntry(msgStr, msgId, value));
                break;
            case kEntryUpdate:
                this.msgId = extractUInt16(1);
                this.seqNum = extractUInt16(3);
                this.dataType = NTDataType.getFromFlag(data[5]);
                this.value = extractValue(6);
                NTStorage.ENTRIES.get(msgId).setValue(value);
                break;
            case kServerHello:
            case kExecuteRpc:
            case kRpcResponse:
                this.msgId = extractUInt16(1);
                this.msgStr = readString(3);
                break;
            default:
                this.msgId = extractUInt16(1);
                break;
        }
        NTStorage.PACKET_DATA.add(this);
    }

    private int extractUInt16(int start) {
        usedLength += 2;
        return NumberUtils.getUInt16(ArrayUtils.sliceArr(data, start, start + 2));
    }

    private Object extractValue(int start) {
        switch (dataType) {
            case NT_BOOLEAN:
                usedLength++;
                return data[start] == 0x01;
            case NT_DOUBLE:
                usedLength += 8;
                return NumberUtils.getDouble(ArrayUtils.sliceArr(data, start, start + 8));
            case NT_STRING:
                return readString(start);
        }
        return null;
    }

    private String readString(int start) {
        int strLen = NumberUtils.readULEB128(ArrayUtils.sliceArr(data, start));
        usedLength += strLen;
        return new String(ArrayUtils.sliceArr(data, start, start + strLen));
    }

    public NTMessageType getMessageType() {
        return messageType;
    }

    public int getMsgId() {
        return msgId;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public int getFlags() {
        return flags;
    }

    public NTDataType getDataType() {
        return dataType;
    }

    public String getMsgStr() {
        return msgStr;
    }

    public int usedLength() {
        return usedLength;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }
}
