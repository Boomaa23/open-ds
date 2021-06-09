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

    public NTPacketData(byte[] data) {
        if (data.length < 2) {
            usedLength = Integer.MAX_VALUE;
        }
        this.usedLength = 1;
        this.data = data;
        try {
            this.messageType = NTMessageType.getFromFlag(NumberUtils.getUInt8(data[0]));
            switch (messageType) {
                case kEntryAssign:
                    this.msgStr = readString(usedLength);
                    this.dataType = NTDataType.getFromFlag(NumberUtils.getUInt8(data[usedLength++]));
                    this.msgId = extractUInt16(usedLength);
                    this.seqNum = extractUInt16(usedLength);
                    boolean persistent = NumberUtils.getUInt8(data[usedLength++]) == 0x01;
                    this.value = extractValue(usedLength);
                    //TODO add tables and nesting
                    NTStorage.ENTRIES.put(msgId, new NTEntry(msgStr, msgId, dataType, value, persistent));
                    break;
                case kEntryUpdate:
                    this.msgId = extractUInt16(usedLength);
                    NTEntry toUpdate = NTStorage.ENTRIES.get(msgId);
                    this.seqNum = extractUInt16(usedLength);
                    this.dataType = NTDataType.getFromFlag(NumberUtils.getUInt8(data[usedLength++]));
                    this.value = extractValue(usedLength);
                    toUpdate.setValue(value);
                    break;
                case kServerHello:
                    int serverFlag = NumberUtils.getUInt8(data[usedLength++]);
                    NTConnection.SERVER_SEEN_CLIENT = serverFlag == 0x01;
                    NTConnection.SERVER_IDENTITY = readString(usedLength);
                    this.value = NTConnection.SERVER_IDENTITY;
                    break;
                case kFlagsUpdate:
                    this.msgId = extractUInt16(usedLength);
                    int entryFlag = NumberUtils.getUInt8(data[usedLength++]);
                    NTStorage.ENTRIES.get(msgId).setPersistent(entryFlag == 0x01);
                    break;
                case kClientHello:
                    int clientProtoRev = extractUInt16(usedLength);
                    String clientIdentity = readString(usedLength);
                    NTStorage.CLIENTS.put(clientIdentity, clientProtoRev);
                    this.value = clientIdentity;
                    break;
                case kProtoUnsup:
                    NTConnection.SERVER_LATEST_VER = extractUInt16(usedLength);
                    break;
                case kEntryDelete:
                    this.msgId = extractUInt16(usedLength);
                    NTStorage.ENTRIES.remove(msgId);
                    break;
                case kClearEntries:
                    final int[] checkAgainst = new int[] {0xD0, 0x6C, 0xB2, 0x7A};
                    byte[] received = ArrayUtils.sliceArr(data, usedLength, usedLength + 4);
                    usedLength += 4;
                    boolean doReset = true;
                    for (int i = 0; i < checkAgainst.length; i++) {
                        if (received[i] != (byte) checkAgainst[i]) {
                            doReset = false;
                            break;
                        }
                    }
                    if (doReset) {
                        NTStorage.ENTRIES.clear();
                        NTStorage.TABS.clear();
                    }
                    break;
                case kExecuteRpc:
                case kRpcResponse:
                    break;
            }
            NTStorage.PACKET_DATA.add(this);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            NTConnection.CUTOFF_DATA = data;
        }
    }

    private int extractUInt16(int start) {
        usedLength += 2;
        return NumberUtils.getUInt16(ArrayUtils.sliceArr(data, start, start + 2));
    }

    private Object extractValue(int start) {
        switch (dataType) {
            case NT_BOOLEAN:
                usedLength++;
                return NumberUtils.getUInt8(data[start]) == 0x01;
            case NT_DOUBLE:
                return readDouble(start);
            case NT_STRING:
                return readString(start);
            case NT_STRING_ARRAY:
                usedLength++;
                int len = NumberUtils.getUInt8(data[start]);
                String[] strs = new String[len];
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
        int strLen = NumberUtils.decodeULEB128(ArrayUtils.sliceArr(data, start));
        int encLen = NumberUtils.sizeULEB128(strLen);
        usedLength += strLen + encLen;
        start += encLen;
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
