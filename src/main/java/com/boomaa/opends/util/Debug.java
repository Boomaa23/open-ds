package com.boomaa.opends.util;

import com.boomaa.opends.display.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Debug {
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final List<String> stickyMessages = new LinkedList<>();

    private Debug() {
    }

    public static void println(String msg, EventSeverity severity, boolean sticky, boolean forced) {
        if (forced || Parameter.DEBUG.isPresent()) {
            if (sticky && stickyMessages.contains(msg)) {
                return;
            }
            String dt = LocalDateTime.now().format(TS_FORMAT);
            String out = String.format("%s [%s]: %s", dt, severity.name(), msg);
            Logger.OUT.println(out);
            DSLog.queueEvent(msg, severity);
            if (severity == EventSeverity.ERROR) {
                System.err.println(out);
            } else {
                System.out.println(out);
            }
            stickyMessages.add(msg);
        }
    }

    public static void println(String msg, EventSeverity severity, boolean sticky) {
        println(msg, severity, sticky, false);
    }

    public static void println(String msg, EventSeverity severity) {
        println(msg, severity, false, false);
    }

    public static void println(String msg) {
        println(msg, EventSeverity.INFO, false, false);
    }

    public static boolean removeSticky(String msg) {
        return stickyMessages.remove(msg);
    }
}
