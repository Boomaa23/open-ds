package com.boomaa.opends.util;

public abstract class Clock extends Thread {
    public static final int INSTANT = 0;
    protected final int msToCycle;
    protected boolean done = false;

    public Clock(String name, int msToCycle) {
        this.msToCycle = msToCycle;
        super.setName(name);
    }

    public Clock(int msToCycle) {
        this.msToCycle = msToCycle;
        String name = getClass().getSimpleName();
        if (!name.isEmpty()) {
            super.setName(name);
        }
    }

    public abstract void onCycle();

    @Override
    public void run() {
        while (!done) {
            try {
                Thread.sleep(msToCycle);
            } catch (InterruptedException ignored) {
                break;
            }
            onCycle();
        }
        super.run();
    }

    public void end() {
        this.done = true;
    }

    public boolean isDone() {
        return done;
    }
}
