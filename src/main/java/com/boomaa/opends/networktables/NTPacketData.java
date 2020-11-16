package com.boomaa.opends.networktables;

import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

import java.util.Objects;

public class NTPacketData {
    private byte[] data;
    private NTMessageType messageType;
    private int msgId = -1;
    private int seqNum = -1;
    private NTDataType dataType;
    private String msgStr = "null";
    private Object value = "null";
    private int usedLength;

    //TODO move from NTv2 to v3, change begin packets to 0x01, 0x03, 0x00 & do uleb128 encoding
    public NTPacketData(byte[] data) {
        if (data.length < 2) {
            usedLength = Integer.MAX_VALUE;
        }
        this.data = data;
        this.messageType = NTMessageType.getFromFlag(data[0]);
        this.usedLength = 1;
        switch (messageType) {
            case kEntryAssign:
                this.msgStr = readString(usedLength);
                this.dataType = NTDataType.getFromFlag(data[usedLength++]);
                this.msgId = extractUInt16(usedLength);
                this.seqNum = extractUInt16(usedLength);
                this.value = extractValue(usedLength);
                //TODO add tables and nesting
                NTStorage.ENTRIES.put(msgId, new NTEntry(msgStr, msgId, dataType, value));
                break;
            case kEntryUpdate:
                this.msgId = extractUInt16(usedLength);
                NTEntry toUpdate = NTStorage.ENTRIES.get(msgId);
                this.seqNum = extractUInt16(usedLength);
                this.dataType = toUpdate.getDataType();
                this.value = extractValue(usedLength);
                toUpdate.setValue(value);
                break;
            case kServerHello:
            case kExecuteRpc:
            case kRpcResponse:
                this.msgId = extractUInt16(1);
                this.msgStr = readString(3);
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
                return readDouble(start);
            case NT_STRING:
                return readString(start);
            case NT_STRING_ARRAY:
                usedLength++;
                String[] strs = new String[data[start]];
                for (int i = 0; i < strs.length; i++) {
                    strs[i] = readString(usedLength);
                }
                return strs;
            case NT_DOUBLE_ARRAY:
                usedLength++;
                double[] dbls = new double[data[start]];
                for (int i = 0; i < dbls.length; i++) {
                    dbls[i] = readDouble(usedLength);
                }
                return dbls;
        }
        return null;
    }

    private double readDouble(int start) {
        usedLength += 8;
        return NumberUtils.getDouble(ArrayUtils.sliceArr(data, start, start + 8));
    }

    private String readString(int start) {
        int strLen = extractUInt16(start);
        start += 2;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NTPacketData that = (NTPacketData) o;
        return msgId == that.msgId &&
                messageType == that.messageType &&
                dataType == that.dataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageType, msgId, dataType);
    }
}
