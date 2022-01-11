package com.boomaa.opends.networking;

import com.boomaa.opends.display.MainJDEC;

public class AddressConstants {
    public static final String LOCALHOST = "localhost";
    public static final String USB_RIO_IP = "172.22.11.2";
    public static final String FMS_IP = "10.0.100.5";
    private static final PortTriple FMS_PORTS_2020 = new PortTriple(1750, 1160, 1121);
    private static final PortQuad RIO_PORTS_2020 = new PortQuad(1740, 1110, 1150, 1735);

    public static PortTriple getFMSPorts() {
        return (PortTriple) getProtoYearValue("FMS_PORTS");
    }

    public static PortQuad getRioPorts() {
        return (PortQuad) getProtoYearValue("RIO_PORTS");
    }

    public static String getRioAddress(boolean isUSB) throws NumberFormatException {
        if (isUSB) {
            return USB_RIO_IP;
        }
        int teamNum = MainJDEC.TEAM_NUMBER.checkedIntParse();
        if (teamNum != -1) {
            return "roboRIO-" + teamNum +"-FRC.local";
        } else if (MainJDEC.TEAM_NUMBER.getText().equalsIgnoreCase("localhost")) {
            return LOCALHOST;
        }
        // Default case that will always fail
        return "route-find-fail";
    }

    private static Object getProtoYearValue(String base) {
        try {
            return AddressConstants.class.getDeclaredField(base + "_" + MainJDEC.getProtocolYear()).get(null);
        } catch (NoSuchFieldException e0) {
            try {
                return AddressConstants.class.getDeclaredField(base + "_2020").get(null);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return null;
    }
}
