package com.boomaa.opends.display.frames;

import com.boomaa.opends.display.Logger;
import com.boomaa.opends.display.PopupBase;

public class LogFrame extends PopupBase {
    @Override
    public void config() {
        super.config();
        super.setTitle("Log");
        content.add(Logger.PANE);
    }
}
