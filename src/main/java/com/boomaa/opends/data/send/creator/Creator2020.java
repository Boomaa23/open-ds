package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.display.frames.JoystickFrame;

public class Creator2020 extends PacketCreator {
    @Override
    public byte[] dsToRioUdp() {
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

        if (SEQUENCE_COUNTER.getCounter() <= 10) {
            builder.addBytes(SendTag.DATE.getBytes());
            builder.addBytes(SendTag.TIMEZONE.getBytes());
        }
        if (IS_ENABLED.isSelected()) {
            builder.addBytes(SendTag.JOYSTICK.getBytes());
        }

        return builder.build();
    }

    @Override
    public byte[] dsToRioTcp() {
        PacketBuilder builder = new PacketBuilder();
        if (JoystickFrame.EmbeddedJDEC.CLOSE_BTN.wasPressed()) {
            builder.addBytes(SendTag.JOYSTICK_DESC.getBytes());
        }
        if (FMS_TYPE.getSelectedItem() != FMSType.NONE) {
            builder.addBytes(SendTag.MATCH_INFO.getBytes());
        }
        if (!GAME_DATA.getText().isEmpty()) {
            builder.addBytes(SendTag.GAME_DATA.getBytes());
        }
        return builder.build();
    }

    @Override
    public byte[] dsToFmsUdp() {
        //TODO add fms content
        return new byte[0];
    }

    @Override
    public byte[] dsToFmsTcp() {
        //TODO add fms content
        return new byte[0];
    }
}
