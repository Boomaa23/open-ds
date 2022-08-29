package com.boomaa.opends.headless;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.JFrame;

public class HFrame extends HideBase<JFrame> {
    private boolean visible = false;

    public HFrame(String title) {
        super(() -> new JFrame(title));
    }

    public void setIconImage(Image image) {
        if (!isHeadless()) {
            getElement().setIconImage(image);
        }
    }

    public void setDefaultCloseOperation(int operation) {
        if (!isHeadless()) {
            getElement().setDefaultCloseOperation(operation);
        }
    }

    public void setResizable(boolean resizable) {
        if (!isHeadless()) {
            getElement().setResizable(resizable);
        }
    }

    public void pack() {
        if (!isHeadless()) {
            getElement().pack();
        }
    }

    public void setLocationRelativeTo(Component c) {
        if (!isHeadless()) {
            getElement().setLocationRelativeTo(c);
        }
    }

    public void setPreferredSize(Dimension preferredSize) {
        if (!isHeadless()) {
            getElement().setPreferredSize(preferredSize);
        }
    }

    public void setVisible(boolean b) {
        if (!isHeadless()) {
            getElement().setVisible(b);
        } else {
            visible = b;
        }
    }

    public boolean isShowing() {
        return isHeadless() ? visible : getElement().isShowing();
    }

    public void add(Component comp) {
        if (!isHeadless()) {
            getElement().add(comp);
        }
    }

    public void add(Component comp, Object constraints) {
        if (!isHeadless()) {
            getElement().add(comp, constraints);
        }
    }

    public Container getContentPane() {
        return getElement().getContentPane();
    }
}
