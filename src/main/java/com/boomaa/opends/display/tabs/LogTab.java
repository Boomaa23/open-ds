package com.boomaa.opends.display.tabs;

import com.boomaa.opends.display.Logger;

import java.awt.Dimension;

public class LogTab extends TabBase {
    public LogTab() {
        super(new Dimension(400, 270));
        Logger.PANE.setPreferredSize(super.dimension);
    }

    @Override
    public void config() {
        super.add(Logger.PANE);
    }
}
