package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MainFrame;
import com.boomaa.opends.util.OperatingSystem;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public abstract class PopupBase extends JFrame {
    private static List<Class<?>> alive = new ArrayList<>();
    private final Dimension dimension;
    protected Container content;

    public PopupBase() {
        this("PopupBase", new Dimension(640, 480));
    }

    public PopupBase(String title, Dimension dimension) {
        super(title);
        alive.add(this.getClass());
        this.dimension = dimension;
        this.content = this.getContentPane();
        config();
        display();
    }

    public void config() {
        //default config goes here
        this.setPreferredSize(dimension);
        this.setIconImage(MainFrame.FIRST_LOGO);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void display() {
        this.pack();
        this.setVisible(true);
    }

    public static boolean isAlive(Class<?> clazz) {
        return alive.contains(clazz);
    }

    public static void removeAlive(Class<?> clazz) {
        alive.remove(clazz);
    }

    @Override
    public void dispose() {
        if (OperatingSystem.isWindows()) {
            super.dispose();
        } else {
            setVisible(true);
        }
        alive.remove(this.getClass());
    }
}
