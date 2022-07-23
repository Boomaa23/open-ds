package com.boomaa.opends.util;

import com.boomaa.opends.display.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class Debug {
    private static final List<String> stickyMessages = new LinkedList<>();

    private Debug() {
    }

    public static void println(String msg, EventSeverity severity, boolean toEvents, boolean toPane) {
        if (Parameter.DEBUG.isPresent()) {
            //TODO fixed width spacing, i.e. align everything in the stdout
            String dt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String out = String.format("DEBUG - %s: %s : %s", severity.name(), dt, msg);
            if (toPane) {
                Logger.OUT.println(out);
            }
            if (toEvents) {
                DSLog.queueEvent(msg, severity);
            }
            System.out.println(out);
        }
    }

    public static void println(String msg, EventSeverity severity) {
        println(msg, severity, true, true);
    }

    public static void println(String msg) {
        println(msg, EventSeverity.INFO);
    }

    public static boolean printlnSticky(String msg, EventSeverity severity) {
        if (!stickyMessages.contains(msg)) {
            println(msg, severity);
            return stickyMessages.add(msg);
        }
        return false;
    }

    public static boolean removeSticky(String msg) {
        return stickyMessages.remove(msg);
    }
}
