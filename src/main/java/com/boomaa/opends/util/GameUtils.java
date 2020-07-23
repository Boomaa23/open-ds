package com.boomaa.opends.util;

public class GameUtils {
    public static byte getAlliance(boolean isBlue, int num) {
        return (byte) (num + (isBlue ? 3 : 0));
    }
}
