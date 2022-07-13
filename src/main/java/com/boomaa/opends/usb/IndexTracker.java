package com.boomaa.opends.usb;

import java.util.Arrays;

public class IndexTracker {
    public static final int MAX_JS_NUM = 6; //max 6 joysticks
    public static int MAX_JS_INDEX = 0;
    private static final boolean[] tracker = new boolean[MAX_JS_NUM];

    private IndexTracker() {
    }

    public static int registerNext() {
        for (int i = 0; i < tracker.length; i++) {
            if (!tracker[i]) {
                if (i > MAX_JS_INDEX) {
                    MAX_JS_INDEX = i;
                }
                tracker[i] = true;
                return i;
            }
        }
        return -1;
    }

    public static void register(int index) {
        tracker[index] = true;
        checkMax();
    }

    public static void unregister(int index) {
        tracker[index] = false;
        checkMax();
    }

    private static void checkMax() {
        for (int i = 0; i < tracker.length; i++) {
            if (tracker[i]) {
                MAX_JS_INDEX = i;
            }
        }
    }

    public static void reset() {
        Arrays.fill(tracker, false);
    }

    public static boolean isRegistered(int index) {
        return tracker[index];
    }
}
