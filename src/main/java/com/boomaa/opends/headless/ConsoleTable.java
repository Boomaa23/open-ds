package com.boomaa.opends.headless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsoleTable {
    //TODO rework for suppliers (i.e. be able to have values that will auto-update when the CTable is printed)
    protected final List<String[]> rows;
    protected final int numCols;
    protected final boolean useRowDividers;
    protected final String[] headerRow;
    protected final int[] colMaxWidths;
    protected final Map<String, Integer> leftColMap;
    private String asString;

    public ConsoleTable(boolean useRowDividers, String... headerRow) {
        this.useRowDividers = useRowDividers;
        this.headerRow = headerRow;
        this.rows = new ArrayList<>();
        this.leftColMap = new HashMap<>();
        this.numCols = headerRow.length;
        this.colMaxWidths = new int[numCols];

        for (int i = 0; i < numCols; i++) {
            colMaxWidths[i] = headerRow[i].length();
        }
        recreateString();
    }

    public ConsoleTable(String... headerRow) {
        this(false, headerRow);
    }

    public ConsoleTable set(int r, int c, String value) {
        if (c == 0) {
            leftColMap.put(rows.get(r)[c], r);
        }
        rawSet(r, c, value);
        findColMaxWidths(c);
        recreateString();
        return this;
    }

    private void rawSet(int r, int c, String value) {
        if (c >= numCols) {
            throw new IndexOutOfBoundsException(String.format("Table does not have %s columns", c));
        }

        while (r >= rows.size()) {
            rows.add(new String[numCols]);
        }
        rows.get(r)[c] = value;
    }

    private void findColMaxWidths(int c) {
        for (String[] row : rows) {
            int colLen = row[c].length();
            if (colLen > colMaxWidths[c]) {
                colMaxWidths[c] = colLen;
            }
        }
    }

    public int getRowIdx(String leftColValue) {
        Integer retval = leftColMap.get(leftColValue);
        return retval == null ? -1 : retval;
    }

    public ConsoleTable appendRow(String... values) {
        if (values.length != numCols) {
            throw new IndexOutOfBoundsException(
                    String.format("Column index %s out of bounds for length %s",
                            values.length, numCols));
        }
        leftColMap.put(values[0], rows.size());
        rows.add(values);
        for (int c = 0; c < values.length; c++) {
            findColMaxWidths(c);
        }
        recreateString();
        return this;
    }

    private void recreateString() {
        StringBuilder sb = new StringBuilder();
        for (int r = -1; r < rows.size(); r++) {
            if (r == -1) {
                appendDividerLine(sb, '=');
                appendPaddedRow(sb, headerRow);
                appendDividerLine(sb, '=');
            } else {
                appendPaddedRow(sb, rows.get(r));
                if (useRowDividers || r == rows.size() - 1) {
                    appendDividerLine(sb, '-');
                }
            }
        }
        asString = sb.toString();
    }

    private void appendPaddedRow(StringBuilder sb, String[] row) {
        for (int c = 0; c < row.length; c++) {
            sb.append('|').append(' ').append(row[c]);
            for (int j = 0; j < colMaxWidths[c] - row[c].length(); j++) {
                sb.append(' ');
            }
            sb.append(' ');
        }
        sb.append("|\n");
    }

    private void appendDividerLine(StringBuilder sb, char c) {
        for (int maxWidth : colMaxWidths) {
            sb.append('+');
            // + 2 is for padding left and right (min +1 on each side)
            for (int i = 0; i < maxWidth + 2; i++) {
                sb.append(c);
            }
        }
        sb.append("+\n");
    }

    @Override
    public String toString() {
        return asString;
    }
}
