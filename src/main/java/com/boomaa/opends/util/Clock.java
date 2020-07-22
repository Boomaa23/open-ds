package com.boomaa.opends.util;

public class Clock extends Thread {
    private final int msToCycle;
    private final Runnable onCycle;

    public Clock(int msToCycle, Runnable onCycle) {
        this.msToCycle = msToCycle;
        this.onCycle = onCycle;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(msToCycle);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onCycle.run();
        super.run();
    }
}
