package com.boomaa.opends.display;

public class ProtocolClass {
    private final String baseClass;
    private int year = -1;

    public ProtocolClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public ProtocolClass update() {
        this.year = MainJDEC.getProtocolYear();
        return this;
    }

    public ProtocolClass setYear(int year) {
        this.year = year;
        return this;
    }

    @Override
    public String toString() {
        if (year == -1) {
            year = MainJDEC.getProtocolYear();
        }
        return baseClass + year;
    }
}
