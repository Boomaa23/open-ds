package com.boomaa.opends.display;

public enum FMSType {
    NONE, SIMULATED, REAL;

    @Override
    public String toString() {
        String uname = super.toString();
        return Character.toUpperCase(uname.charAt(0)) + uname.substring(1).toLowerCase();
    }
}
