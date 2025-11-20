package com.boomaa.opends.display;

import com.boomaa.opends.data.holders.Protocol;
import com.boomaa.opends.data.holders.Remote;

import java.lang.reflect.Constructor;

public class PPConstructorStore {
    private static final String[] subparserKeys = new String[] {
            "RioToDsUdp",
            "RioToDsTcp",
            "FmsToDsUdp",
            "FmsToDsTcp"
    };
    private final ProtocolClass parserClass;
    private final Constructor<?>[] subparserCtor = new Constructor[subparserKeys.length];

    public PPConstructorStore(ProtocolClass parserClass) {
        this.parserClass = parserClass;
        update();
    }

    public Constructor<?> get(Protocol protocol, Remote remote) {
        return subparserCtor[(remote.ordinal() * 2) + protocol.ordinal()];
    }

    public void update() {
        try {
            for (int i = 0; i < subparserKeys.length; i++) {
                String subparserClassName = String.format("%s$%s", parserClass, subparserKeys[i]);
                subparserCtor[i] = Class.forName(subparserClassName).getConstructor(byte[].class);
            }
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
