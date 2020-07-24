package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.JDEC;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.frames.FMSFrame;

public class Creator2020 extends PacketCreator implements JDEC {
    @Override
    public byte[] dsToRioUdp(SendTag... tags) {
        PacketBuilder builder = getSequenced();
        builder.addInt(0x01);
        int control = (ESTOP_BTN.wasPressed() ? Control.ESTOP.getFlag() : 0)
                + (PopupBase.isAlive(FMSFrame.class) ? Control.FMS_CONNECTED.getFlag() : 0)
                + (ENABLE_BTN.isSelected() ? Control.ENABLED.getFlag() : 0);
        if (TELEOP_MODE_BTN.isSelected()) {
            control += Control.TELEOP_MODE.getFlag();
        } else if (AUTO_MODE_BTN.isSelected()) {
            control += Control.AUTO_MODE.getFlag();
        } else if (TEST_MODE_BTN.isSelected()) {
            control += Control.TEST_MODE.getFlag();
        }
        builder.addInt(control);
        int request = 0x00;
        if (RESTART_ROBO_RIO_BTN.wasPressed()) {
            request += Request.REBOOT_ROBO_RIO.getFlag();
        }
        if (RESTART_CODE_BTN.wasPressed()) {
            request += Request.RESTART_CODE.getFlag();
        }
        builder.addInt(request);
        int allianceSided = -1;
        if (ALLIANCE_ONE.isSelected()) {
            allianceSided = 0;
        } else if (ALLIANCE_TWO.isSelected()) {
            allianceSided = 1;
        } else if (ALLIANCE_THREE.isSelected()) {
            allianceSided = 2;
        }
        builder.addInt(new AllianceStation(allianceSided, BLUE_ALLIANCE_BTN.isSelected()).getGlobalNum());
        for (SendTag tag : tags) {
            builder.addBytes(tag.getBytes());
        }
        return builder.build();
    }

    @Override
    public byte[] dsToRioTcp(SendTag... tags) {
        return new byte[0];
    }

    @Override
    public byte[] dsToFmsUdp(SendTag... tags) {
        return new byte[0];
    }

    @Override
    public byte[] dsToFmsTcp(SendTag... tags) {
        return new byte[0];
    }
}
