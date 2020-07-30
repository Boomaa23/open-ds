package com.boomaa.opends.display.layout;

import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.elements.GBCPanelBuilder;

public abstract class LayoutPlacer implements MainJDEC {
    protected final GBCPanelBuilder base;

    public LayoutPlacer(GBCPanelBuilder base) {
        this.base = base;
    }

    public abstract void init();

    public GBCPanelBuilder getBase() {
        return base;
    }
}
