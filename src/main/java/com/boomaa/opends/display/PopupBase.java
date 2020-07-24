package com.boomaa.opends.display;

import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;

public abstract class PopupBase extends JFrame {
    private static List<Class<?>> alive = new ArrayList<>();

    public PopupBase() {
        alive.add(this.getClass());
    }

    public static boolean isAlive(Class<?> clazz) {
        return alive.contains(clazz);
    }
}
