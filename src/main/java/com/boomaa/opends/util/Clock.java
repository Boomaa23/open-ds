package com.boomaa.opends.util;

public abstract class Clock extends Thread {
    private final int msToCycle;

    public Clock(int msToCycle) {
        this.msToCycle = msToCycle;
    }

    public abstract void onCycle();

    @Override
    public void run() {
        try {
            Thread.sleep(msToCycle);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onCycle();
        super.run();
    }
}
