package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MainFrame;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PopupBase extends JFrame {
    protected static Map<String, PopupBase> alive = new LinkedHashMap<>();
    protected final String uuid;
    protected final Dimension dimension;
    protected Container content;

    public PopupBase() {
        this("PopupBase", new Dimension(640, 480));
    }

    public PopupBase(String title, Dimension dimension) {
        super(title);
        this.uuid = getClass().getSimpleName();
        alive.put(uuid, this);
        this.dimension = dimension;
        this.content = this.getContentPane();
        config();
        super.pack();
        super.setLocationRelativeTo(null);
        forceShow();
    }

    public void config() {
        //default config goes here
        this.setPreferredSize(dimension);
        this.setIconImage(MainFrame.FIRST_LOGO);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void forceShow() {
        super.setVisible(true);
    }

    public void forceHide() {
        super.setVisible(false);
    }

    public String getUUID() {
        return uuid;
    }

    public static boolean isAlive(String uuid) {
        return alive.containsKey(uuid);
    }

    public static PopupBase getAlive(String uuid) {
        return alive.get(uuid);
    }

    public static void removeAlive(String uuid) {
        alive.remove(uuid);
    }

    @Override
    public void dispose() {
        forceHide();
    }
}
