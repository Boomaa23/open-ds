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
        //TODO make this work correctly
        return (long) ((bytes[3] * Math.pow(256, 3)) + (bytes[2] * Math.pow(256, 2)) + (bytes[1] * 256) + bytes[0]);
    }

    public static int getUInt8(byte num) {
        return num & 0xFF;
    }

    public static int getUInt16(byte[] nums) {
        return ((nums[0] & 0xFF) << 8) | (nums[1] & 0xFF);
    }

    public static int getUInt16(int[] nums) {
        int out = 0;
        for (int i = 0; i < nums.length; i++) {
            out += Math.pow(2, (nums.length - i - 1)) * nums[i];
        }
        //TODO implement pdp log port status get
        // make this work correctly
        // ref https://frcture.readthedocs.io/en/latest/driverstation/rio_to_ds.html#pdp-log-0x08
        return out;
    }

    public static boolean hasMaskMatch(byte b, int flag, int... bitmaskPos) {
        char[] bin = padByteString(Integer.toBinaryString(b)).toCharArray();
        StringBuilder maskedStr = new StringBuilder();
        boolean putMask = false;
        for (int i = 0; i < bin.length; i++) {
            for (int mask : bitmaskPos) {
                if (i == mask) {
                    maskedStr.append(bin[i]);
                    putMask = true;
                }
            }
            if (!putMask) {
                maskedStr.append('0');
            }
            putMask = false;
        }
        return Integer.parseInt(maskedStr.toString(), 2) == flag;
    }

    public static boolean hasPlacedBit(byte b, int bitmaskPos) {
        char[] bin = padByteString(Integer.toBinaryString(b)).toCharArray();
        return bin[bitmaskPos] == 1;
    }

    public static String padByteString(String byteStr) {
        if (byteStr.length() < 8) {
            StringBuilder sb = new StringBuilder(byteStr);
            while(sb.length() < 8) {
                sb.insert(0, "0");
            }
            byteStr = sb.toString();
        }
        return byteStr;
    }

    // Reverses all the bits in a byte. Used to convert MSB 0 into LSB 0 for button encoding
    public static int reverseByte(int in) {
        in = (in & 0xF0) >> 4 | (in & 0x0F) << 4;
        in = (in & 0xCC) >> 2 | (in & 0x33) << 2;
        in = (in & 0xAA) >> 1 | (in & 0x55) << 1;
        return in;
    }
}
