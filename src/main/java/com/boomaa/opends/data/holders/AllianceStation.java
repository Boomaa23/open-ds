package com.boomaa.opends.data.holders;

import com.boomaa.opends.util.NumberUtils;

public class AllianceStation {
    private final int sidedNum;
    private final boolean isBlue;
    private String status;

    public AllianceStation(int sidedZeroedNum, boolean isBlue) {
        this.sidedNum = sidedZeroedNum;
        this.isBlue = isBlue;
    }

    public AllianceStation setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public int getSidedNum() {
        return sidedNum + 1;
    }

    public int getGlobalNum() {
        return sidedNum + (isBlue ? 3 : 0);
    }

    public static AllianceStation getFromByte(byte b) {
        int num = NumberUtils.getUInt8(b);
        return new AllianceStation(num, num >= 3);
    }
}
