package com.boomaa.opends.display;

import com.boomaa.opends.util.Debug;

import java.util.Arrays;

public class MultiKeyEvent {
    private final Runnable action;
    private final int[] keycodes;
    private final boolean[] flags;

    public MultiKeyEvent(Runnable action, int... keycodes) {
        this.action = action;
        this.keycodes = keycodes;
        this.flags = new boolean[keycodes.length];
    }

    public void update(int keyCode, boolean pressed) {
        for (int i = 0; i < keycodes.length; i++) {
            if (keycodes[i] == keyCode) {
                flags[i] = pressed;
                return;
            }
        }
    }

    public void pollAction() {
        for (boolean flag : flags) {
            if (!flag) {
                return;
            }
        }
        String fmtAction = "Multikey action %s with keycodes: " + Arrays.toString(keycodes);
        Debug.println(String.format(fmtAction, "detected"));
        action.run();
        Debug.println(String.format(fmtAction, "run"));
    }
}
