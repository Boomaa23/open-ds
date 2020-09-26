package com.boomaa.opends.data.holders;

import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.util.ArrayUtils;
import com.boomaa.opends.util.NumberUtils;

import java.util.Calendar;

public class Date {
    private final int year; // actual year, exported as # from 1900
    private final int month; // 0-12
    private final int day; // 1-31
    private final int hour;
    private final int minute;
    private final int second;
    private final int microsecond;

    public Date(int year, int month, int day, int hour, int minute, int second, int microsecond) {
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

    public int getMicrosecond() {
        return microsecond;
    }

    public byte[] toSendBytes() {
        PacketBuilder builder = new PacketBuilder();
        builder.addBytes(NumberUtils.intToByteQuad(microsecond));
        builder.addInt(second);
        builder.addInt(minute);
        builder.addInt(hour);
        builder.addInt(day);
        builder.addInt(month);
        builder.addInt(getAdjustedYear());
        return builder.build();
    }

    public static Date fromRecvBytes(byte[] bytes) {
        return new Date(
                bytes[9] + 1900, bytes[8], bytes[7], bytes[6], bytes[5], bytes[4],
                NumberUtils.getUInt32(ArrayUtils.sliceArr(bytes, 0, 4))
        );
    }

    public static Date now() {
        Calendar cal = Calendar.getInstance();
        return new Date(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND) * 1000
        );
    }

    public enum DayMap {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

        public static DayMap getFromInt(int index) {
            return DayMap.values()[index];
        }
    }
}
