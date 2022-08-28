package com.boomaa.opends.display;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StdRedirect {
    public static StdRedirect ERR = new StdRedirect(true);
    public static StdRedirect OUT = new StdRedirect(false);
    private final PrintStream original;
    private final boolean isError;

    private StdRedirect(boolean isError) {
        this.original = isError ? System.err : System.out;
        this.isError = isError;
    }

    public void reset() {
        if (isError) {
            System.setErr(original);
        } else {
            System.setOut(original);
        }
    }

    public void to(PrintStream redirect) {
        if (isError) {
            System.setErr(redirect);
        } else {
            System.setOut(redirect);
        }
    }

    public void toNull() {
        to(new NullPrintStream());
    }

    private static class NullPrintStream extends PrintStream {
        public NullPrintStream() {
            super(new NullByteArrayOutputStream());
        }
    }

    private static class NullByteArrayOutputStream extends ByteArrayOutputStream {
        @Override
        public void write(int b) {
        }

        @Override
        public void write(byte[] b, int off, int len) {
        }

        @Override
        public void writeTo(OutputStream out) {
        }
    }
}
