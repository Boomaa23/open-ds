package com.boomaa.opends.display;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StdOutRedirect {
    private static final PrintStream stdOut = System.out;

    private StdOutRedirect() {
    }

    public static void reset() {
        System.setOut(stdOut);
    }

    public static void to(PrintStream redirect) {
        System.setOut(redirect);
    }

    public static void toNull() {
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
