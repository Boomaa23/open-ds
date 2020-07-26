package com.boomaa.opends.display.elements;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.MainFrame;

import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBCPanelBuilder {
    private final GridBagConstraints gbc;

    public GBCPanelBuilder(GridBagConstraints gbc) {
        this.gbc = gbc;
    }

    public GBCPanelBuilder(GBCPanelBuilder panel) {
        this(panel.getGridBagConstraints());
    }

    public GBCPanelBuilder() {
        this(new GridBagConstraints());
    }

    public GBCPanelBuilder setPos(int x, int y, int width, int height) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        return this;
    }

    public GBCPanelBuilder setX(int x) {
        gbc.gridx = x;
        return this;
    }

    public GBCPanelBuilder setY(int y) {
        gbc.gridy = y;
        return this;
    }

    public GBCPanelBuilder setWidth(int width) {
        gbc.gridwidth = width;
        return this;
    }

    public GBCPanelBuilder setHeight(int height) {
        gbc.gridheight = height;
        return this;
    }

    public GBCPanelBuilder setAnchor(int anchor) {
        gbc.anchor = anchor;
        return this;
    }

    public GBCPanelBuilder setFill(int fill) {
        gbc.fill = fill;
        return this;
    }

    public GBCPanelBuilder setInsets(Insets insets) {
        gbc.insets = insets;
        return this;
    }

    public void build(Component... comps) {
        if (comps.length > 1) {
            MainJDEC.CONTENT.add(MainFrame.addToPanel(new JPanel(), comps), gbc);
        } else {
            MainJDEC.CONTENT.add(comps[0], gbc);
        }
    }

    public GBCPanelBuilder clone() {
        return new GBCPanelBuilder()
                .setX(gbc.gridx).setY(gbc.gridy)
                .setWidth(gbc.gridwidth).setHeight(gbc.gridheight)
                .setAnchor(gbc.anchor).setFill(gbc.fill).setInsets(gbc.insets);
    }

    public GridBagConstraints getGridBagConstraints() {
        return gbc;
    }
}
