package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

import java.lang.reflect.Constructor;

public class PPConstructorStore {
    private final ProtocolClass parserClass;
    private final Constructor<?>[] constructors = new Constructor[4];

    public PPConstructorStore(ProtocolClass parserClass) {
        this.parserClass = parserClass;
        update();
    }

    public Constructor<?> get(Protocol protocol, Remote remote) {
        return constructors[(remote.ordinal() * 2) + protocol.ordinal()];
    }

    public void update() {
        try {
            constructors[0] = Class.forName(parserClass + "$RioToDsUdp").getConstructor(byte[].class);
            constructors[1] = Class.forName(parserClass + "$RioToDsTcp").getConstructor(byte[].class);
            constructors[2] = Class.forName(parserClass + "$FmsToDsUdp").getConstructor(byte[].class);
            constructors[3] = Class.forName(parserClass + "$FmsToDsTcp").getConstructor(byte[].class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
