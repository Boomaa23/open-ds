package com.boomaa.opends.data.holders;

public class AllianceStation {
    private final int sidedNum;
    private final boolean isBlue;
    private Status status;

    public AllianceStation(int sidedZeroedNum, boolean isBlue) {
        this.sidedNum = sidedZeroedNum;
        this.isBlue = isBlue;
    }

    public AllianceStation setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public int getSidedNum() {
        return sidedNum + 1;
    }

    public int getGlobalNum() {
        return sidedNum + (isBlue ? 3 : 0);
    }

    public boolean isBlue() {
        return isBlue;
    }

    @Override
    public String toString() {
        return "AllianceStation{" +
                "sidedNum=" + sidedNum +
                ", isBlue=" + isBlue +
                ", status=" + status +
                '}';
    }

    public static AllianceStation getFromByte(byte b) {
        return new AllianceStation(b, b >= 3);
    }

    public enum Status {
        GOOD, BAD, WAITING, INVALID
    }
}
