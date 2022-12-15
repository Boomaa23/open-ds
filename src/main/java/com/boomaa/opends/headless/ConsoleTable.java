package com.boomaa.opends.headless;

import com.boomaa.opends.util.Debug;
import com.boomaa.opends.util.EventSeverity;

import java.util.function.Supplier;

public class ConsoleTable {
    protected final Entry[][] entries;
    protected final int[] colWidths;
    protected final int rows;
    protected final int cols;
    protected final boolean useRowDividers;
    protected String asString;

    public ConsoleTable(int rows, int cols, boolean useRowDividers) {
        this.rows = rows;
        this.cols = cols;
        this.useRowDividers = useRowDividers;
        this.entries = new Entry[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                entries[r][c] = new Entry(r, c, this);
            }
        }
        this.colWidths = new int[cols];
    }

    public ConsoleTable(int rows, int cols) {
        this(rows, cols, false);
    }

    public Entry getEntry(int row, int col) {
        if (isValidCell(row, col)) {
            return entries[row][col];
        } else {
            Debug.println(String.format("Table entry location (%d, %d) out of bounds for (%d, %d) max)",
                row, col, rows - 1, cols - 1), EventSeverity.ERROR);
        }
        return null;
    }

    private boolean isValidCell(int row, int col) {
        return row < rows && col < cols;
    }

    public void setCol(int startRow, int startCol, String... values) {
        int ctr = startRow;
        for (String v : values) {
            Entry e = getEntry(ctr, startCol);
            if (e != null) {
                e.setValue(v);
                ctr++;
            } else {
                Debug.println("No entry found at (" + ctr + ", " + startCol + ")", EventSeverity.ERROR);
                return;
            }
        }
    }

    @SafeVarargs
    public final void setCol(int startRow, int startCol, Supplier<String>... values) {
        int ctr = startRow;
        for (Supplier<String> v : values) {
            Entry e = getEntry(ctr, startCol);
            if (e != null) {
                e.setSupplier(v);
                ctr++;
            } else {
                Debug.println("No entry found at (" + ctr + ", " + startCol + ")", EventSeverity.ERROR);
                return;
            }
        }
    }

    public void setRow(int startRow, int startCol, String... values) {
        int ctr = startCol;
        for (String v : values) {
            Entry e = getEntry(startRow, ctr);
            if (e != null) {
                e.setValue(v);
                ctr++;
            } else {
                Debug.println("No entry found at (" + startRow + ", " + ctr + ")", EventSeverity.ERROR);
                return;
            }
        }
    }

    @SafeVarargs
    public final void setRow(int startRow, int startCol, Supplier<String>... values) {
        int ctr = startCol;
        for (Supplier<String> v : values) {
            Entry e = getEntry(startRow, ctr);
            if (e != null) {
                e.setSupplier(v);
                ctr++;
            } else {
                Debug.println("No entry found at (" + startRow + ", " + ctr + ")", EventSeverity.ERROR);
                return;
            }
        }
    }

    public void updateAll() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                getEntry(r, c).update();
            }
        }
        recreateString();
    }

    private void updateMaxWidths(int col, int newLength) {
        if (newLength > colWidths[col]) {
            colWidths[col] = newLength;
        } else {
            colWidths[col] = 0;
            for (int r = 0; r < rows; r++) {
                Entry entry = entries[r][col];
                int len = entry.length();
                if (len > colWidths[col]) {
                    colWidths[col] = len;
                }
            }
        }
    }

    public void recreateString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            if (r == 0) {
                appendDividerLine(sb, '=');
                appendPaddedRow(sb, entries[r]);
                appendDividerLine(sb, '=');
            } else {
                appendPaddedRow(sb, entries[r]);
                if (useRowDividers || r == rows - 1) {
                    appendDividerLine(sb, '-');
                }
            }
        }
        asString = sb.toString();
    }

    private void appendPaddedRow(StringBuilder sb, Entry[] row) {
        for (int c = 0; c < row.length; c++) {
            sb.append('|').append(' ').append(row[c]);
            for (int j = 0; j < colWidths[c] - row[c].length(); j++) {
                sb.append(' ');
            }
            sb.append(' ');
        }
        sb.append("|\n");
    }

    private void appendDividerLine(StringBuilder sb, char c) {
        for (int maxWidth : colWidths) {
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
        recreateString();
        return asString;
    }

    public static class Entry {
        private final int row;
        private final int col;
        private final ConsoleTable parent;
        private String value;
        private Supplier<String> supplier;

        public Entry(int row, int col, ConsoleTable parent) {
            this.row = row;
            this.col = col;
            this.parent = parent;
        }

        public String getValue() {
            return value;
        }

        public Entry setValue(String value) {
            this.value = value;
            parent.updateMaxWidths(col, length());
            return this;
        }

        public Entry setSupplier(Supplier<String> supplier) {
            this.supplier = supplier;
            return this;
        }

        public Entry update() {
            if (supplier != null) {
                value = supplier.get();
                parent.updateMaxWidths(col, length());
            }
            return this;
        }

        public int length() {
            return getValue() == null ? 0 : getValue().length();
        }

        @Override
        public String toString() {
            return getValue() == null ? "" : getValue();
        }
    }
}
