package com.boomaa.opends.util;

public class ArrayUtils {
    public static byte[] sliceArr(byte[] array, int start, int end) {
        byte[] out = new byte[end - start];
        try {
            System.arraycopy(array, start, out, 0, out.length);
            //TODO figure out why this doesn't work: 28 2c 01 04 32 0c 2d 00 22 05 02 42 83 94 5e 00 00 00 00 00 00 00 00 3e ab be 15 42 77 68 81 00 00 00 00 00 00 00 00 3e 96 59 29
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return out;
    }

    public static byte[] sliceArr(byte[] array, int start) {
        return sliceArr(array, start, array.length);
    }

    public static int[] sliceArr(int[] array, int start, int end) {
        int[] out = new int[end - start];
        System.arraycopy(array, start, out, 0, out.length);
        return out;
    }

    public static int[] sliceArr(int[] array, int start) {
        return sliceArr(array, start, array.length);
    }

    public static Byte[] byteBox(byte[] bytes) {
        Byte[] out = new Byte[bytes.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = bytes[i];
        }
        return out;
    }
}
