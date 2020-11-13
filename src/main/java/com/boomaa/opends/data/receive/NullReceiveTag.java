package com.boomaa.opends.data.receive;

public class NullReceiveTag implements ReceiveTagAction<String> {
    private static final NullReceiveTag INSTANCE = new NullReceiveTag();

    @Override
    public TagValueMap<String> getValue(byte[] packet, int size) {
        return null;
    }

    @Override
    public TagValueMap<String> getValue(byte[] packet) {
        return null;
    }

    public static NullReceiveTag getInstance() {
        return INSTANCE;
    }
}
