package com.boomaa.opends.display;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public abstract class PopupBase extends JFrame {
    private static List<Class<?>> alive = new ArrayList<>();

    public PopupBase() {
        alive.add(this.getClass());
        display(new Dimension(400, 400), false, true);
    }

    public void display(Dimension dimension, boolean resizable, boolean center) {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(dimension);
        this.setResizable(resizable);
        this.pack();
        if (center) {
            this.setLocationRelativeTo(null);
        }
        this.setVisible(true);
    }

    public static boolean isAlive(Class<?> clazz) {
        return alive.contains(clazz);
    }
}
