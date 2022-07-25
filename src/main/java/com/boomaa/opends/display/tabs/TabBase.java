package com.boomaa.opends.display.tabs;

import com.boomaa.opends.display.frames.FrameBase;
import com.boomaa.opends.util.Debug;

import java.awt.Dimension;
import javax.swing.JPanel;

public abstract class TabBase extends JPanel {
    private static Class<? extends TabBase> visible;
    protected final Dimension dimension;

    public TabBase(Dimension dimension) {
        this.dimension = dimension;
        if (dimension != null) {
            FrameBase.applyNonWindowsScaling(dimension);
            this.setPreferredSize(dimension);
        }
        Debug.println(getClass().getSimpleName() + " configuration started");
        config();
        Debug.println(getClass().getSimpleName() + " configured");
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
