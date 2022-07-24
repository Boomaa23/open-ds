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

    public static void println(String msg, Options options) {
        if (options.forceDisplay() || Parameter.DEBUG.isPresent()) {
            if (options.isSticky() && stickyMessages.contains(msg)) {
                return;
            }
            String dt = LocalDateTime.now().format(TS_FORMAT);
            String out = String.format("%s [%s]: %s", dt, options.getSeverity().name(), msg);
            if (options.addToPane()) {
                Logger.OUT.println(out);
            }
            if (options.addToEvents()) {
                DSLog.queueEvent(msg, options.getSeverity());
            }
            if (options.getSeverity() == EventSeverity.ERROR) {
                System.err.println(out);
            } else {
                System.out.println(out);
            }
            stickyMessages.add(msg);
        }
    }

    public static void println(String msg) {
        println(msg, Options.DEFAULT);
    }

    public static boolean removeSticky(String msg) {
        return stickyMessages.remove(msg);
    }

    public static class Options {
        public static final Options DEFAULT = Options.create().setMutable(false);

        private EventSeverity severity;
        private boolean toEvents;
        private boolean toPane;
        private boolean sticky;
        private boolean forced;
        private boolean mutable;

        public Options(EventSeverity severity, boolean toEvents,
                       boolean toPane, boolean sticky,
                       boolean forced, boolean mutable) {
            this.severity = severity;
            this.toEvents = toEvents;
            this.toPane = toPane;
            this.sticky = sticky;
            this.forced = forced;
            this.mutable = mutable;
        }

        public Options(Options other) {
            this(other.severity, other.toEvents, other.toPane, other.sticky, other.forced, other.mutable);
        }

        public EventSeverity getSeverity() {
            return severity;
        }

        public boolean addToEvents() {
            return toEvents;
        }

        public boolean addToPane() {
            return toPane;
        }

        public boolean isSticky() {
            return sticky;
        }

        public boolean forceDisplay() {
            return forced;
        }

        public boolean isMutable() {
            return mutable;
        }

        public Options setSeverity(EventSeverity severity) {
            if (mutable) {
                this.severity = severity;
                return this;
            } else {
                return new Options(this).setMutable(true).setSeverity(severity);
            }
        }

        public Options setToEvents(boolean toEvents) {
            if (mutable) {
                this.toEvents = toEvents;
                return this;
            } else {
                return new Options(this).setMutable(true).setToEvents(toEvents);
            }
        }

        public Options setToPane(boolean toPane) {
            if (mutable) {
                this.toPane = toPane;
                return this;
            } else {
                return new Options(this).setMutable(true).setToPane(toPane);
            }
        }

        public Options setSticky(boolean sticky) {
            if (mutable) {
                this.sticky = sticky;
                return this;
            } else {
                return new Options(this).setMutable(true).setSticky(sticky);
            }
        }

        public Options setForced(boolean forced) {
            if (mutable) {
                this.forced = forced;
                return this;
            } else {
                return new Options(this).setMutable(true).setForced(forced);
            }
        }

        public Options setMutable(boolean mutable) {
            this.mutable = mutable;
            return this;
        }

        public static Options create() {
            return new Options(EventSeverity.INFO, true, true, false, false, true);
        }
    }
}
