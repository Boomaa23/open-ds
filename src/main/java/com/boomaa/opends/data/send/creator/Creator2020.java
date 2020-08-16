package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.FMSType;
import com.boomaa.opends.display.PopupBase;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.display.frames.FMSFrame;
import com.boomaa.opends.display.frames.JoystickFrame;
import com.boomaa.opends.util.NumberUtils;

public class Creator2020 extends PacketCreator {
    @Override
    public byte[] dsToRioUdp() {
        PacketBuilder builder = getSequenced(Remote.ROBO_RIO);
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

        if (SEQUENCE_COUNTER_RIO.getCounter() <= 10) {
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
        PacketBuilder builder = getSequenced(Remote.ROBO_RIO);
        builder.addInt(0x00);
        int status = 0;
        if (ESTOP_BTN.wasPressed()) {
            status += 0x80;
        }
        if (ROBOT_CONNECTION_STATUS.isDisplayed()) {
            status += 0x20;
        }
        if (DisplayEndpoint.NET_IF_INIT.isOrInit(Remote.ROBO_RIO)) {
            status += 0x10 + 0x08;
        }
        if (IS_ENABLED.isSelected()) {
            status += 0x04;
        }
        switch ((RobotMode) ROBOT_DRIVE_MODE.getSelectedItem()) {
            case TEST:
                status += 0x01;
                break;
            case AUTONOMOUS:
                status += 0x02;
                break;
        }
        builder.addInt(status);

        String teamStr = TEAM_NUMBER.getText();
        if (teamStr != null && !teamStr.isEmpty()) {
            builder.addBytes(NumberUtils.intToBytePair(Integer.parseInt(teamStr)));
        }
        if (DisplayEndpoint.NET_IF_INIT.isOrInit(Remote.ROBO_RIO)) {
            double bat = Double.parseDouble(BAT_VOLTAGE.getText().replaceAll(" V", ""));
            //TODO test if this battery re-encoder works
            int b1 = (int) bat;
            int b2 = (int) ((bat - b1) * 256);
            builder.addInt(b1);
            builder.addInt(b2);
        }
        //TODO add FMS tags
        return builder.build();
    }

    @Override
    public byte[] dsToFmsTcp() {
        //TODO add fms content
        PacketBuilder builder = new PacketBuilder();
        builder.addBytes(SendTag.TEAM_NUMBER.getBytes());
        return builder.size() != 0 ? builder.build() : SendTag.DS_PING.getBytes();
    }
}
