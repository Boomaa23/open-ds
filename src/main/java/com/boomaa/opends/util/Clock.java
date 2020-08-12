package com.boomaa.opends.util;

public abstract class Clock extends Thread {
    protected final int msToCycle;
    protected boolean done = false;

    public Clock(int msToCycle) {
        this.msToCycle = msToCycle;
    }

    public abstract void onCycle();

    @Override
    public void run() {
        while (!done) {
            try {
                Thread.sleep(msToCycle);
            } catch (InterruptedException ignored) {
            }
            onCycle();
        }
        super.run();
    }

    public void end() {
        this.done = true;
    }
}
