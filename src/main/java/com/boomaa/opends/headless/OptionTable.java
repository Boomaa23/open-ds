package com.boomaa.opends.headless;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OptionTable extends ConsoleTable {
    private static final char[] KEYCODE_SET = new char[] {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    };
    private final Map<Character, Supplier<OperationReturn>> operationMap = new HashMap<>();
    private final boolean useValueCol;
    private int lastOptionRow;

    public OptionTable(int rows, boolean useValueCol, boolean useRowDividers) {
        super(rows, useValueCol ? 3 : 2, useRowDividers);
        this.useValueCol = useValueCol;
        getEntry(0, 0).setValue("Name");
        getEntry(0, 1).setValue("Keycode");
        if (useValueCol) {
            getEntry(0, 2).setValue("Value");
        }
        this.lastOptionRow = 1;
    }

    public OptionTable appendOption(String option, Supplier<OperationReturn> operation, Supplier<String> supplier) {
        if (lastOptionRow >= KEYCODE_SET.length) {
            throw new IndexOutOfBoundsException("OptionTable is full (36 values). Create a new table or reduce.");
        }
        getEntry(lastOptionRow, 0).setValue(option);
        getEntry(lastOptionRow, 1).setValue(String.valueOf(KEYCODE_SET[lastOptionRow - 1]));
        if (useValueCol) {
            if (supplier != null) {
                getEntry(lastOptionRow, 2).setSupplier(supplier);
            }
        }
        operationMap.put(KEYCODE_SET[lastOptionRow - 1], operation);
        lastOptionRow++;
        return this;
    }

    public OptionTable appendOption(String option, Supplier<OperationReturn> operation) {
        return appendOption(option, operation, null);
    }

    public OperationReturn runOperation(char keycode) {
        Supplier<OperationReturn> operation = operationMap.get(keycode);
        return operation != null ? operation.get() : null;
    }
}
