package com.boomaa.opends.data.send.creator;

import com.boomaa.opends.data.holders.AllianceStation;
import com.boomaa.opends.data.holders.Control;
import com.boomaa.opends.data.holders.Remote;
import com.boomaa.opends.data.holders.Request;
import com.boomaa.opends.data.send.PacketBuilder;
import com.boomaa.opends.data.send.SendTag;
import com.boomaa.opends.display.RobotMode;
import com.boomaa.opends.usb.Joystick;
import com.boomaa.opends.usb.USBInterface;

import java.util.zip.CRC32;

public class Creator2014 extends NoTCPCreator {
    @Override
    public byte[] dsToRioUdp() {
        PacketBuilder builder = getSequenced(Remote.ROBO_RIO);
        int control = 0x40;
        if (ESTOP_BTN.wasPressed()) {
            control = Control.ESTOP.getFlag();
        } else if (RESTART_ROBO_RIO_BTN.wasPressed()) {
            control = Request.REBOOT_ROBO_RIO.getFlag();
        } else {
            control |= ((IS_ENABLED.isSelected() ? 0x20 : 0x00) +
                    ((RobotMode) ROBOT_DRIVE_MODE.getSelectedItem()).getControlFlag().getFlag());
//            if (resync) {
//                control |= 0x04;
//            }
            if (FMS_CONNECTION_STATUS.isDisplayed()) {
                control |= Control.FMS_CONNECTED.getFlag();
            }
        }
        builder.addInt(control)
                .addInt(0x00) // number of computer digital inputs
                .addInt((TEAM_NUMBER.checkedIntParse() & 0xFF00) >> 8)
                .addInt(TEAM_NUMBER.checkedIntParse() & 0xFF);
        AllianceStation station = new AllianceStation(ALLIANCE_NUM.getSelectedIndex(), ALLIANCE_COLOR.getSelectedItem().equals("Blue"));
        builder.addInt(station.isBlue() ? 0x42 : 0x52)
                .addInt(station.getSidedNum() + 0x30);
        if (IS_ENABLED.isSelected()) {
            USBInterface.findControllers();
            USBInterface.updateValues();
            for (int i = 0; i < Joystick.MAX_JS_NUM; i++) {
                builder.addBytes(SendTag.JOYSTICK.getBytes());
            }
        }
        int[] dsVerFlags = { 0x31, 0x34, 0x30, 0x32, 0x31, 0x37, 0x30, 0x30 };
        for (int i = 0; i < dsVerFlags.length; i++) {
            builder.setInt(72 + i, dsVerFlags[i]);
        }
        CRC32 crc = new CRC32();
        crc.update(builder.build());
        long checksum = crc.getValue();
        builder.setInt(1020, (int) ((checksum & 0xFF000000) >> 24))
                .setInt(1021, (int) ((checksum & 0xFF0000) >> 16))
                .setInt(1022, (int) ((checksum & 0xFF00) >> 8))
                .setInt(1023, (int) (checksum & 0xFF));

        return builder.build();
    }

    @Override
    public byte[] dsToFmsUdp() {
        // No 2014 FMS packet creation in LibDS
        return new byte[0];
    }
}
