package com.boomaa.opends.data.send;

public class NullSendTag implements SendTagData {
    private static NullSendTag INSTANCE = new NullSendTag();

    private NullSendTag() {
    }

    @Override
    public byte[] getTagData() {
        return new byte[0];
    }

    public static NullSendTag get() {
        return INSTANCE;
    }
}
