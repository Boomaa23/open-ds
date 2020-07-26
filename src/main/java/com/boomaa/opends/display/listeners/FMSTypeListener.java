package com.boomaa.opends.display.listeners;

import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.networking.SimulatedFMS;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FMSTypeListener implements ItemListener, MainJDEC {
    private FMSFrame frame;
    private SimulatedFMS networking;

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            FMSType type = (FMSType) FMS_TYPE.getSelectedItem();
            switch (type) {
                case SIMULATED:
                    frame = new FMSFrame();
                    networking = new SimulatedFMS();
                    break;
                case NONE:
                case REAL:
                    if (frame != null) {
                        frame.dispose();
                        networking.close();
                    }
                    break;
            }
        }
    }
}
