package com.boomaa.opends.display.tabs;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;

public abstract class TabBase extends JPanel {
    private static Class<? extends TabBase> visible;
    protected final Dimension dimension;

    public TabBase(Dimension dimension) {
        this.dimension = dimension;
        if (dimension != null) {
            this.setPreferredSize(dimension);
        }
        config();
    }

    public TabBase() {
        this(null);
    }

    public abstract void config();

    public static boolean isVisible(Class<? extends TabBase> clazz) {
        return clazz == visible;
    }

    public static void setVisible(Class<? extends TabBase> clazz) {
        visible = clazz;
    }
}
