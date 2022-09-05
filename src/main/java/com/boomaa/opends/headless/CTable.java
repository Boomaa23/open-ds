package com.boomaa.opends.headless;

import java.util.ArrayList;
import java.util.List;

public class CTable {
    private final List<String[]> rows;
    private final int numCols;
    private final boolean useRowDividers;
    private final String[] headerRow;
    private final int[] colMaxWidths;
    private String asString;

    public CTable(boolean useRowDividers, String... headerRow) {
        this.useRowDividers = useRowDividers;
        this.headerRow = headerRow;
        this.rows = new ArrayList<>();
        this.numCols = headerRow.length;
        this.colMaxWidths = new int[numCols];

        for (int i = 0; i < numCols; i++) {
            colMaxWidths[i] = headerRow[i].length();
        }
        recreateString();
    }

    public CTable(String... headerRow) {
        this(false, headerRow);
    }

    public CTable set(int r, int c, String value) {
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

    public CTable appendRow(String... values) {
        if (values.length != numCols) {
            throw new IndexOutOfBoundsException(
                    String.format("Column index %s out of bounds for length %s",
                            values.length, numCols));
        }

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
