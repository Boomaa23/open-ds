package com.boomaa.opends.util;

public class InitChecker {
    private boolean rio = false;
    private boolean fms = false;

    public void setFms(boolean fms) {
        this.fms = fms;
    }

    public void setRio(boolean rio) {
        this.rio = rio;
    }

    public boolean getRio() {
        return rio;
    }

    public boolean getFms() {
        return fms;
    }

    public boolean isInit() {
        return rio && fms;
    }
}
