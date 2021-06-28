package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.DisplayEndpoint;
import com.boomaa.opends.display.MainJDEC;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.ControlDevices;
import com.boomaa.opends.util.NumberUtils;

public class Creator2015 extends NoTCPCreator {
    @Override
    public byte[] dsToRioUdp() {
        PacketBuilder builder = getSequenced(Remote.ROBO_RIO);
        builder.addInt(0x01);

        int control = ((RobotMode) MainJDEC.ROBOT_DRIVE_MODE.getSelectedItem()).getControlFlag().getFlag();
        if (MainJDEC.FMS_CONNECTION_STATUS.isDisplayed()) {
            control |= Control.FMS_CONNECTED.getFlag();
        }
        if (MainJDEC.ESTOP_BTN.wasPressed()) {
            control |= Control.ESTOP.getFlag();
        }
        if (MainJDEC.IS_ENABLED.isSelected()) {
            control |= Control.ENABLED.getFlag();
        }
        builder.addInt(control);

        builder.addInt((MainJDEC.RESTART_ROBO_RIO_BTN.wasPressed() ? Request.REBOOT_ROBO_RIO.getFlag() : 0) +
                (MainJDEC.RESTART_CODE_BTN.wasPressed() ? Request.RESTART_CODE.getFlag() : 0));
        builder.addInt(new AllianceStation(ALLIANCE_NUM.getSelectedIndex(), ALLIANCE_COLOR.getSelectedItem().equals("Blue")).getGlobalNum());

        //TODO add proper timezone data request-passing
        if (SEQUENCE_COUNTER_RIO.getCounter() <= 10) {
            byte[] tzIn = SendTag.TIMEZONE.getBytes();
            byte[] tzOut = new byte[tzIn.length - 1];
            tzOut[0] = tzIn[0];
            tzOut[1] = tzIn[1];
            System.arraycopy(tzIn, 3, tzOut, 0, tzIn.length - 3);
            builder.addBytes(SendTag.DATE.getBytes())
                    .addInt(tzIn.length)
                    .addBytes(tzOut);
        }
        if (IS_ENABLED.isSelected()) {
            ControlDevices.findAll();
            ControlDevices.updateValues();
            for (int i = 0; i < Joystick.MAX_JS_NUM; i++) {
                builder.addBytes(SendTag.JOYSTICK.getBytes());
            }
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
        //TODO add FMS tags (not needed?)
        return builder.build();
    }
}
