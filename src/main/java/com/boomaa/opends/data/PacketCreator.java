package com.boomaa.opends.data;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.tags.SendTag;
import com.boomaa.opends.display.FMSWindow;
import com.boomaa.opends.display.JDEC;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.util.SequenceCounter;

public class PacketCreator implements JDEC {
    public static final SequenceCounter SEQUENCE_COUNTER = new SequenceCounter();

    public static byte[] dsToRio(SendTag... tags) {
        PacketBuilder builder = getSequenced();
        builder.addInt(0x01);
        int control = (ESTOP_BTN.wasPressed() ? Control.ESTOP.getFlag() : 0)
                + (PopupBase.isAlive(FMSWindow.class) ? Control.FMS_CONNECTED.getFlag() : 0)
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

    public static PacketBuilder getSequenced() {
        return new PacketBuilder().addBytes(SEQUENCE_COUNTER.increment().getBytes());
    }
}
