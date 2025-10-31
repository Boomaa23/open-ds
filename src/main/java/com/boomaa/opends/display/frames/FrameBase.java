package com.boomaa.opends.display.frames;

import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.OperatingSystem;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;

public abstract class FrameBase extends JFrame {
    public static final double NONWINDOWS_WIDTH_SCALE = 1.2;
    protected static Map<Class<? extends FrameBase>, FrameBase> alive = new LinkedHashMap<>();
    protected final String uuid;
    protected final Dimension dimension;
    protected Container content;

    public FrameBase() {
        this("FrameBase", new Dimension(640, 480));
    }

    public FrameBase(String title, Dimension dimension) {
        super(title);
        this.uuid = getClass().getSimpleName();
        alive.put(getClass(), this);
        this.dimension = dimension;
        applyNonWindowsScaling(dimension);
        this.content = this.getContentPane();
        Debug.println(title + " frame configuration started");
        preConfig();
        Debug.println(title + " frame configured");
        super.pack();
        super.setLocationRelativeTo(null);
        forceShow();
        postConfig();
    }

    public void preConfig() {
        // Default config goes here
        this.setPreferredSize(dimension);
        this.setIconImage(MainFrame.ICON);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void postConfig() {
    }

    public void forceShow() {
        super.setVisible(true);
        Debug.println(getTitle() + " frame displayed");
    }

    public void forceHide() {
        super.setVisible(false);
        Debug.println(getTitle() + " frame hidden");
    }

    public String getUUID() {
        return uuid;
    }

    public static boolean isAlive(Class<?> uuid) {
        return alive.containsKey(uuid);
    }

    public static boolean isVisible(Class<?> uuid) {
        return alive.containsKey(uuid) && alive.get(uuid).isShowing();
    }

    @SuppressWarnings("unchecked")
    public static <T extends FrameBase> T getAlive(Class<?> uuid) {
        return (T) alive.get(uuid);
    }

    public static void removeAlive(Class<?> uuid) {
        alive.remove(uuid);
    }

    @Override
    public void dispose() {
        forceHide();
    }

    public void forceDispose() {
        removeActionListeners();
        removeAlive(this.getClass());
        super.dispose();
    }

    public void removeActionListeners() {
        for (Component component : content.getComponents()) {
            if (component instanceof JButton) {
                JButton button = ((JButton) component);
                for (ActionListener listener : button.getActionListeners().clone()) {
                    button.removeActionListener(listener);
                }
            }
        }
    }

    public static void applyNonWindowsScaling(Dimension dimension) {
        if (!OperatingSystem.isWindows()) {
            dimension.setSize(dimension.getWidth() * NONWINDOWS_WIDTH_SCALE, dimension.getHeight());
        }
    }
}
