package com.boomaa.opends.networking;

import com.boomaa.opends.display.MainJDEC;

public interface AddressConstants {
    String FMS_IP = "10.0.100.5";
    PortTriple FMS_PORTS = new PortTriple(1750, 1160, 1121);
    PortTriple RIO_PORTS = new PortTriple(1740, 1110, 1150);

    static String getRioAddress() {
        return "roboRIO-" + Integer.parseInt(MainJDEC.TEAM_NUMBER.getText()) + "-FRC.local";
    }

    String LOCALHOST = "localhost";
}
