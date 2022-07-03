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
        //TODO fix busy-waiting (improve clock logic/efficiency)
        while (!done) {
            Clock.sleep(msToCycle);
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

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
