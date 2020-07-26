package com.boomaa.opends.display;

public class ProtocolClass {
    private final String baseClass;
    private int year;

    public ProtocolClass(String baseClass) {
        this.baseClass = baseClass;
        this.year = (Integer) MainJDEC.PROTOCOL_YEAR.getSelectedItem();
    }

    public ProtocolClass update() {
        this.year = (Integer) MainJDEC.PROTOCOL_YEAR.getSelectedItem();
        return this;
    }

    public ProtocolClass setYear(int year) {
        this.year = year;
        return this;
    }

    @Override
    public String toString() {
        return baseClass + year;
    }
}
