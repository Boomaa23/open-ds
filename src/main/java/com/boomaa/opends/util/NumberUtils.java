package com.boomaa.opends.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class NumberUtils {
    public static float getFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public static int getInt32(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static long getUInt32(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static int getUInt8(byte num) {
        return num & 0xFF;
    }

    public static int getUInt16(byte[] nums) {
        return ((nums[0] & 0xff) << 8) | (nums[1] & 0xff);
    }

    public static int getUInt16(int[] nums) {
        //TODO implement pdp log port status get
        // ref https://frcture.readthedocs.io/en/latest/driverstation/rio_to_ds.html#pdp-log-0x08
        return -1;
    }
}
