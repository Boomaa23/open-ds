package com.boomaa.opends.display;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Logger extends OutputStream {
    public static Logger LOGGER;
    public static PrintStream OUT;
    public static JScrollPane PANE;
    private final byte[] oneByte;
    private Appender appender;

    static {
        JTextArea textArea = new JTextArea(15, 45);
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textArea.setCaretPosition(textArea.getDocument().getLength());
                textArea.update(textArea.getGraphics());
            }
        });
        textArea.setEditable(false);

        LOGGER = new Logger(textArea);
        OUT = new PrintStream(LOGGER);
        PANE = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public Logger(JTextArea textArea) {
        oneByte = new byte[1];
        appender = new Appender(textArea);
    }

    public Appender getAppender() {
        return appender;
    }

    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    public synchronized void close() {
        appender = null;
    }

    public synchronized void flush() {
    }

    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
    }

    private static String bytesToString(byte[] ba, int str, int len) {
        return new String(ba, str, len, StandardCharsets.UTF_8);
    }

    public static class Appender implements Runnable {
        private static final String EOL1 = "\n";
        private static final String EOL2 = System.getProperty("line.separator", EOL1);

        private final JTextArea textArea;
        private final int maxLines = 2000;
        private final LinkedList<Integer> lengths;
        private final List<String> values;
        private int curLength;
        private boolean clear;
        private boolean queue;

        private Appender(JTextArea textArea) {
            this.textArea = textArea;
            lengths = new LinkedList<>();
            values = new ArrayList<>();

            curLength = 0;
            clear = false;
            queue = true;
        }

        public List<String> getValues() {
            return values;
        }

        public JTextArea getTextArea() {
            return textArea;
        }

        public synchronized void append(String val) {
            values.add(val);
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        public synchronized void clear() {
            clear = true;
            curLength = 0;
            lengths.clear();
            values.clear();
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        public synchronized void run() {
            if (clear) {
                textArea.setText("");
            }
            for (String val : values) {
                curLength += val.length();
                if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                    if (lengths.size() >= maxLines) {
                        textArea.replaceRange("", 0, lengths.removeFirst());
                    }
                    lengths.addLast(curLength);
                    curLength = 0;
                }
                textArea.append(val);
            }
            values.clear();
            clear = false;
            queue = true;
        }
    }

}