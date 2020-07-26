package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.frames.FMSFrame;

public class Creator2020 extends PacketCreator implements MainJDEC {
    @Override
    public byte[] dsToRioUdp(SendTag tag) {
        PacketBuilder builder = getSequenced();
        builder.addInt(0x01);
        int control = (ESTOP_BTN.wasPressed() ? Control.ESTOP.getFlag() : 0)
                + (PopupBase.isAlive(FMSFrame.class) ? Control.FMS_CONNECTED.getFlag() : 0)
                + (IS_ENABLED.isSelected() ? Control.ENABLED.getFlag() : 0)
                + ((RobotMode) ROBOT_DRIVE_MODE.getSelectedItem()).getControlFlag().getFlag();
        builder.addInt(control);
        int request = 0x00;
        if (RESTART_ROBO_RIO_BTN.wasPressed()) {
            request += Request.REBOOT_ROBO_RIO.getFlag();
        }
        if (RESTART_CODE_BTN.wasPressed()) {
            request += Request.RESTART_CODE.getFlag();
        }
        builder.addInt(request);
        builder.addInt(new AllianceStation(ALLIANCE_NUM.getSelectedIndex(), ALLIANCE_COLOR.getSelectedItem().equals("Blue")).getGlobalNum());
        if (tag != null) {
            builder.addBytes(tag.getBytes());
        }
        return builder.build();
    }

    @Override
    public byte[] dsToRioTcp(SendTag tag) {
        if (tag != null) {
            byte[] tagData = tag.getValue().getTagData();
            byte[] out = new byte[2 + tagData.length];

            //TODO figure out 2-len size
            tagData[0] = (byte) (out.length << 8);
            tagData[1] = (byte) out.length;
            tagData[2] = (byte) tag.getFlag();
            System.arraycopy(tagData, 0, out, 3, tagData.length);
            return out;
        }
        return new byte[0];
    }

    @Override
    public byte[] dsToFmsUdp(SendTag tag) {
        return new byte[0];
    }

    @Override
    public byte[] dsToFmsTcp(SendTag tag) {
        return new byte[0];
    }
}
