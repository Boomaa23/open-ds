package com.boomaa.opends.data.send;

public class NullSendTag implements SendTagData {
    private static final NullSendTag INSTANCE = new NullSendTag();

    @Override
    public byte[] getTagData() {
        return new byte[0];
    }

    public static NullSendTag getInstance() {
        return INSTANCE;
    }
}
