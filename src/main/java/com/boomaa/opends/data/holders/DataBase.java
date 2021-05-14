package com.boomaa.opends.data.holders;

import com.boomaa.opends.display.MainJDEC;

import java.util.LinkedHashMap;

public class DataBase extends LinkedHashMap<Integer, Integer> {
    private int defFlag = -1;

    public DataBase addFlag(int flag, int... years) {
        return addFlag(flag, false, years);
    }

    public DataBase addFlag(int flag, boolean isDefFlag, int... years) {
        if (isDefFlag) {
            defFlag = flag;
        }
        for (int year : years) {
            super.put(year, flag);
        }
        return this;
    }

    public int getCurrentFlag() {
        Integer flag = super.get(MainJDEC.getProtocolYear());
        return flag != null ? flag : defFlag;
    }

    public static DataBase create() {
        return new DataBase();
    }

    public interface Holder {
        int getFlag();
    }
}
