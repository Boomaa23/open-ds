package com.boomaa.opends.data.holders;

import java.util.Objects;

public class AllianceStation {
    private final int sidedZeroedNum;
    private final boolean isBlue;
    private Status status;

    public AllianceStation(int sidedZeroedNum, boolean isBlue) {
        this.sidedZeroedNum = sidedZeroedNum;
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
        return sidedZeroedNum + 1;
    }

    public int getGlobalNum() {
        return sidedZeroedNum + (isBlue ? 3 : 0);
    }

    public boolean isBlue() {
        return isBlue;
    }

    @Override
    public String toString() {
        return "AllianceStation{"
                + "sidedZeroedNum=" + sidedZeroedNum
                + ", isBlue=" + isBlue
                + ", status=" + status
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AllianceStation that = (AllianceStation) o;
        return sidedZeroedNum == that.sidedZeroedNum
                && isBlue == that.isBlue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sidedZeroedNum, isBlue);
    }

    public static AllianceStation getFromByte(byte b) {
        return new AllianceStation(b % 3, b >= 3);
    }

    public enum Status {
        GOOD, BAD, WAITING, INVALID
    }
}
