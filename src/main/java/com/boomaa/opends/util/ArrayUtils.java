package com.boomaa.opends.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {
    public static byte[] sliceArr(byte[] array, int start, int end) {
        try {
            byte[] out = new byte[end - start];
            System.arraycopy(array, start, out, 0, out.length);
            return out;
        } catch (ArrayIndexOutOfBoundsException | NegativeArraySizeException ignored) {
        }
        return new byte[0];
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

    public static String[] removeBlanks(String[] in) {
        List<String> out = new ArrayList<>();
        for (String str : in) {
            if (!str.isEmpty()) {
                out.add(str);
            }
        }
        return out.toArray(new String[0]);
    }
}
