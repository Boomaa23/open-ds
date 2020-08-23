package com.boomaa.opends.networking;

import com.boomaa.opends.display.MainJDEC;

public class AddressConstants {
    public static final String LOCALHOST = "localhost";
    private static final String FMS_IP_2020 = "10.0.100.5";
    private static final PortTriple FMS_PORTS_2020 = new PortTriple(1750, 1160, 1121);
    private static final PortTriple RIO_PORTS_2020 = new PortTriple(1740, 1110, 1150);

    public static PortTriple getFMSPorts() {
        return (PortTriple) getProtoYearValue("FMS_PORTS");
    }

    public static PortTriple getRioPorts() {
        return (PortTriple) getProtoYearValue("RIO_PORTS");
    }

    public static String getFMSIp() {
        return (String) getProtoYearValue("FMS_IP");
    }

    public static String getRioAddress() {
        return "roboRIO-" + Integer.parseInt(MainJDEC.TEAM_NUMBER.getText()) + "-FRC.local";
    }

    private static Object getProtoYearValue(String base) {
        try {
            return AddressConstants.class.getDeclaredField(base + "_" + MainJDEC.PROTOCOL_YEAR.getSelectedItem()).get(null);
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
