package com.boomaa.opends.display;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashMap;
import java.util.Map;

public class GlobalKeyListener implements NativeKeyListener {
    public static final GlobalKeyListener INSTANCE = new GlobalKeyListener();
    private static final Map<Integer, Runnable> keyMap = new HashMap<>();

    private GlobalKeyListener() {
    }

    public GlobalKeyListener addKeyEvent(int keyCode, Runnable action) {
        keyMap.put(keyCode, action);
        return this;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        doAction(nativeKeyEvent);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    public void doAction(NativeKeyEvent event) {
        if (keyMap.containsKey(event.getKeyCode())) {
            keyMap.get(event.getKeyCode()).run();
        }
    }
}
