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

    public static int getInt8(double in) {
        return (int) (in * 127);
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

    public static boolean hasMaskMatch(byte data, byte flag, int... bitmaskPos) {
        char[] bin = padByte(data).toCharArray();
        StringBuilder maskedStr = new StringBuilder();
        boolean putMask = false;
        for (int i = 0; i < bin.length; i++) {
            for (int mask : bitmaskPos) {
                if (!putMask && i == mask) {
                    maskedStr.append(bin[i]);
                    putMask = true;
                }
            }
            if (!putMask) {
                maskedStr.append('0');
            }
            putMask = false;
        }
        return maskedStr.toString().equals(padByte(flag));
    }

    public static boolean hasPlacedBit(byte b, int bitmaskPos) {
        return padByte(b).toCharArray()[bitmaskPos] == 1;
    }

    public static String padByte(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static double roundTo(double value, int decimals) {
        double pow = Math.pow(10, decimals);
        return Math.round(value * pow) / pow;
    }

    // Reverses all the bits in a byte. Used to convert MSB 0 into LSB 0 for button encoding
    public static int reverseByte(int in) {
        in = (in & 0xF0) >> 4 | (in & 0x0F) << 4;
        in = (in & 0xCC) >> 2 | (in & 0x33) << 2;
        in = (in & 0xAA) >> 1 | (in & 0x55) << 1;
        return in;
    }
}
