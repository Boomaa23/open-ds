package com.boomaa.opends.headless;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OptionTable extends ConsoleTable {
    private static final char[] KEYCODE_SET = new char[] {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    };
    private final Map<Character, Runnable> operationMap = new HashMap<>();
    private boolean useValueCol;

    public OptionTable(boolean useValueCol) {
        super(useValueCol
                ? new String[] { "Name", "Keycode", "Value" }
                : new String[] { "Name", "Keycode" }
        );
        this.useValueCol = useValueCol;
    }

    public OptionTable appendOption(String option, Runnable operation, Supplier<String> valueSupplier) {
        int idx = rows.size();
        if (idx >= KEYCODE_SET.length) {
            throw new IndexOutOfBoundsException("OptionTable is full (36 values). Create a new table or reduce.");
        }
        if (useValueCol) {
            if (valueSupplier == null) {
                throw new IllegalArgumentException("Value supplier must not be null if a value column is used.");
            }
            appendRow(option, String.valueOf(KEYCODE_SET[idx]), valueSupplier.get());
        } else {
            appendRow(option, String.valueOf(KEYCODE_SET[idx]));
        }
        operationMap.put(KEYCODE_SET[idx], operation);
        return this;
    }

    public OptionTable appendOption(String option, Runnable operation) {
        return appendOption(option, operation, null);
    }

    public void runOperation(char keycode) {
        Runnable operation = operationMap.get(keycode);
        if (operation != null) {
            operation.run();
        }
    }
}
