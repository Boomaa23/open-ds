package com.boomaa.opends.networking;

import com.boomaa.opends.display.MainJDEC;

public class AddressConstants {
    public static final String LOCALHOST = "localhost";
    private static final String FMS_IP_2020 = "10.0.100.5";
    private static final PortTriple FMS_PORTS_2020 = new PortTriple(1750, 1160, 1121);
    private static final PortQuad RIO_PORTS_2020 = new PortQuad(1740, 1110, 1150, 1735);

    public static PortTriple getFMSPorts() {
        return (PortTriple) getProtoYearValue("FMS_PORTS");
    }

    public static PortQuad getRioPorts() {
        return (PortQuad) getProtoYearValue("RIO_PORTS");
    }

    public static String getFMSIp() {
        return (String) getProtoYearValue("FMS_IP");
    }

    public static String getRioAddress() throws NumberFormatException {
        return "roboRIO-" + MainJDEC.TEAM_NUMBER.checkedIntParse() + "-FRC.local";
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
