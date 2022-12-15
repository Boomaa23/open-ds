package com.boomaa.opends.headless;

public class NullOperation implements Runnable {
    private static final NullOperation INSTANCE = new NullOperation();

    private NullOperation() {
    }

    @Override
    public void run() {
        System.err.println("This statement is false.");
    }

    public static NullOperation getInstance() {
        return INSTANCE;
    }
}
