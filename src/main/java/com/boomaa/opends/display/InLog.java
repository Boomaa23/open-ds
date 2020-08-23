package com.boomaa.opends.display;

import java.util.function.Supplier;

public class InLog {
    public static final InLog ALWAYS = new InLog(true);
    public static final InLog NEVER = new InLog(false);
    public static final InLog CONDITIONALLY = new InLog();

    private Boolean forcedValue;
    private Supplier<Boolean> condition;

    public InLog() {
    }

    private InLog(Boolean forcedValue) {
        this.forcedValue = forcedValue;
    }

    private InLog(Supplier<Boolean> condition) {
        this.condition = condition;
    }

    public InLog condition(Supplier<Boolean> condition) {
        return new InLog(condition);
    }

    public InLog value(boolean value) {
        return new InLog(value);
    }

    public boolean isInLog() {
        return (forcedValue != null && forcedValue) || (condition != null && condition.get());
    }
}
