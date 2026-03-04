package com.boomaa.opends.display;

import com.boomaa.opends.display.frames.MessageBox;
import com.boomaa.opends.util.ArrayUtils;

import java.lang.reflect.InvocationTargetException;

public class ProtocolClassManager<T extends ProtocolClass> {
    private final String canonicalBaseName;
    private int year = -1;

    public ProtocolClassManager(Class<? super T> baseClass) {
        this.canonicalBaseName = baseClass.getCanonicalName();
    }

    public ProtocolClassManager<T> update() {
        this.year = MainJDEC.getProtocolYear();
        return this;
    }

    public T construct() {
        try {
            return (T) Class.forName(this.toString()).getConstructor().newInstance();
        } catch (NoSuchMethodException | ClassNotFoundException
                 | IllegalAccessException | InstantiationException
                 | InvocationTargetException e) {
            e.printStackTrace();
            MessageBox.show(ArrayUtils.printStackTrace(e, 10), MessageBox.Type.ERROR);
            System.exit(1);
        }
        return null;
    }

    @Override
    public String toString() {
        //TODO support rangified protoclasses i.e. Updater2020to2026
        if (year == -1) {
            update();
        }
        return canonicalBaseName + year;
    }
}