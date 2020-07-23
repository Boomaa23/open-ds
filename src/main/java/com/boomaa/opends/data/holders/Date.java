package com.boomaa.opends.data.holders;

import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

public class Date {
    private final int year; // actual year, exported as # from 1900
    private final int month; // 0-12
    private final int day; // 1-31
    private final int hour;
    private final int minute;
    private final int second;
    private final long microsecond;

    public Date(int year, int month, int day, int hour, int minute, int second, long microsecond) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.microsecond = microsecond;
    }

    public int getActualYear() {
        return year;
    }

    public int getAdjustedYear() {
        return year - 1900;
    }

    public int getHRMonth() {
        return month + 1;
    }

    public int getZeroedMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public long getMicrosecond() {
        return microsecond;
    }

    public byte[] toBytes() {
        //TODO implement to-bytes solution
        return null;
    }

    public static Date fromBytes(byte[] bytes) {
        return new Date(
                NumberUtils.getUInt8(bytes[9]),
                NumberUtils.getUInt8(bytes[8]),
                NumberUtils.getUInt8(bytes[7]),
                NumberUtils.getUInt8(bytes[6]),
                NumberUtils.getUInt8(bytes[5]),
                NumberUtils.getUInt8(bytes[4]),
                NumberUtils.getUInt32(ArrayUtils.sliceArr(bytes, 0, 4))
        );
    }
}
