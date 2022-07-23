package com.boomaa.opends.util;

public enum LogicOperation {
    AND((a, b) -> a && b),
    OR((a, b) -> a || b);

    private final OperationAction action;

    LogicOperation(OperationAction action) {
        this.action = action;
    }

    public boolean apply(boolean a, boolean b) {
        return action.apply(a, b);
    }

    private interface OperationAction {
        boolean apply(boolean a, boolean b);
    }
}
