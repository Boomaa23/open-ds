package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MainFrame;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public abstract class PopupBase extends JFrame {
    private static List<Class<?>> alive = new ArrayList<>();
    protected Container content;

    public PopupBase() {
        this("PopupBase", new Dimension(640, 480), false, true);
    }

    public PopupBase(String title, Dimension dimension, boolean resizable, boolean center) {
        super(title);
        alive.add(this.getClass());
        this.content = this.getContentPane();
        config();
        display(dimension, resizable, center);
    }

    public void config() {
        //default config goes here
        this.setIconImage(MainFrame.FIRST_LOGO);
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
