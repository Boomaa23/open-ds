package com.boomaa.opends.display.listeners;

import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.networking.SimulatedFMS;
import com.boomaa.opends.networking.TCPInterface;
import com.boomaa.opends.networking.UDPInterface;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FMSTypeListener extends DisplayEndpoint implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
        reload(e);
    }

    public static void reload(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (FMS_UDP_INTERFACE != null) {
                FMS_UDP_INTERFACE.close();
            }
            if (FMS_TCP_INTERFACE != null) {
                FMS_TCP_INTERFACE.close();
            }
            FMS_UDP_INTERFACE = null;
            FMS_TCP_INTERFACE = null;
            if (SIM_FMS != null) {
                SIM_FMS.close();
            }

            FMSType type = (FMSType) FMS_TYPE.getSelectedItem();
            switch (type) {
                case SIMULATED:
                    FMS_FRAME = new FMSFrame();
                    SIM_FMS = new SimulatedFMS();
                    break;
                case REAL:
                    if (FMS_FRAME != null) {
                        FMS_FRAME.dispose();
                    }
                    FMS_UDP_INTERFACE = new UDPInterface("10.0.100.5", 1160, 1121);
                    FMS_TCP_INTERFACE = new TCPInterface("10.0.100.5", 1750);
                    break;
            }
        }
    }
}
