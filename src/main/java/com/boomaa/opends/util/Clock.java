package com.boomaa.opends.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Clock {
    public static final int INSTANT = 0;
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(6);
    protected final int msToCycle;
    private ScheduledFuture<?> task;
    protected final String name;
    protected boolean done = false;

    public Clock(String name, int msToCycle) {
        this.msToCycle = msToCycle;
        this.name = name;
    }

    public Clock(int msToCycle) {
        this.msToCycle = msToCycle;
        String clazzName = getClass().getSimpleName();
        this.name = !clazzName.isEmpty() ? clazzName : "Clock";
    }

    public abstract void onCycle();

    public void start() {
        this.task = executor.scheduleAtFixedRate(this::onCycle, 0, msToCycle, TimeUnit.MILLISECONDS);
    }

    public void end() {
        this.done = true;
        if (task != null) {
            task.cancel(true);
        }
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
