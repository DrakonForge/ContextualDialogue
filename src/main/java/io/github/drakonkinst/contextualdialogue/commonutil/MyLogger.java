package io.github.drakonkinst.contextualdialogue.commonutil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Custom logger implementation wrapping around java.util.logging.Logger
 * Allows for several levels of priority, colors the text accordingly,
 * and also displays a precise timestamp.
 */
public class MyLogger {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void initialize(final Level level) {
        for(Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
            handler.close();
        }
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new MyFormatter());
        consoleHandler.setLevel(level);
        logger.addHandler(consoleHandler);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
    }

    // LOG //
    public static void log(final Level level, final String msg) {
        logger.log(level, msg);
    }

    public static void log(final Level level, final boolean msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final char msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final int msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final float msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final double msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final long msg) {
        logger.log(level, "" + msg);
    }

    public static void log(final Level level, final Object msg) {
        if(msg == null) {
            logger.log(level, "null");
        } else {
            logger.log(level, msg.toString());
        }
    }

    public static void log(final Level level) {
        logger.log(level, "");
    }

    // INFO //
    public static void info(final String msg) {
        log(Level.INFO, msg);
    }

    public static void info(final boolean msg) {
        log(Level.INFO, msg);
    }

    public static void info(final char msg) {
        log(Level.INFO, msg);
    }

    public static void info(final int msg) {
        log(Level.INFO, msg);
    }

    public static void info(final float msg) {
        log(Level.INFO, msg);
    }

    public static void info(final double msg) {
        log(Level.INFO, msg);
    }

    public static void info(final long msg) {
        log(Level.INFO, msg);
    }

    public static void info(final Object msg) {
        log(Level.INFO, msg);
    }

    public static void info() {
        log(Level.INFO);
    }

    // FINE //
    public static void fine(final String msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final boolean msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final char msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final int msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final float msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final double msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final long msg) {
        log(Level.FINE, msg);
    }

    public static void fine(final Object msg) {
        log(Level.FINE, msg);
    }


    // FINER //
    public static void finer(final String msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final boolean msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final char msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final int msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final float msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final double msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final long msg) {
        log(Level.FINER, msg);
    }

    public static void finer(final Object msg) {
        log(Level.FINER, msg);
    }

    public static void finer() {
        log(Level.FINER);
    }

    // FINEST //
    public static void finest(final String msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final boolean msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final char msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final int msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final float msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final double msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final long msg) {
        log(Level.FINEST, msg);
    }

    public static void finest(final Object msg) {
        log(Level.FINEST, msg);
    }

    public static void finest() {
        log(Level.FINEST);
    }

    // CONFIG //
    public static void config(final String msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final boolean msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final char msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final int msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final float msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final double msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final long msg) {
        log(Level.CONFIG, msg);
    }

    public static void config(final Object msg) {
        log(Level.CONFIG, msg);
    }

    public static void config() {
        log(Level.CONFIG);
    }

    // WARNING //
    public static void warning(final String msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final boolean msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final char msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final int msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final float msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final double msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final long msg) {
        log(Level.WARNING, msg);
    }

    public static void warning(final Object msg) {
        log(Level.WARNING, msg);
    }

    public static void warning() {
        log(Level.WARNING);
    }

    // SEVERE //
    public static void severe(final String msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final boolean msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final char msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final int msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final float msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final double msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final long msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe(final Object msg) {
        log(Level.SEVERE, msg);
    }

    public static void severe() {
        log(Level.SEVERE);
    }

    // SEVERE WITH EXCEPTION //
    public static void severe(final String msg, final Exception e) {
        logger.log(Level.SEVERE, msg + ": " + e.toString(), e);
    }

    public static void severe(final boolean msg, final Exception e) {
        severe("" + msg, e);
    }

    public static void severe(final char msg, final Exception e) {
        severe("" + msg, e);
    }

    public static void severe(final int msg, final Exception e) {
        severe("" + msg, e);;
    }

    public static void severe(final float msg, final Exception e) {
        severe("" + msg, e);
    }

    public static void severe(final double msg, final Exception e) {
        severe("" + msg, e);
    }

    public static void severe(final long msg, final Exception e) {
        severe("" + msg, e);
    }

    public static void severe(final Object msg, final Exception e) {
        if(msg == null) {
            severe("null", e);
        } else {
            severe(msg.toString(), e);
        }
    }

    public static void severe(final Exception e) {
        logger.log(Level.SEVERE, e.toString(), e);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void testLogger() {
        finest("Finest");
        finer("Finer");
        fine("Fine");
        config("Config");
        info("Info");
        warning("Warning");
        severe("Severe");
    }

    private MyLogger() {}

    // https://stackoverflow.com/questions/6898197/how-can-we-remove-extra-message-in-log-files
    // https://stackoverflow.com/questions/53211694/change-color-and-format-of-java-util-logging-logger-output-in-eclipse
    // https://stackoverflow.com/questions/31415147/how-do-i-log-a-stacktrace-using-javas-logger-class
    private static class MyFormatter extends Formatter {
        // ANSI escape code
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";

        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append(getColorFromLevel(record.getLevel()));

            builder.append("[");
            builder.append(calcDate(record.getMillis()));
            builder.append("] ");

            /*
            builder.append("[");
            builder.append(record.getSourceClassName());
            builder.append("]");
            builder.append(" [");
            builder.append(record.getLevel().getName());
            builder.append("]");
            builder.append(ANSI_WHITE);
            builder.append(" - ");
             */
            builder.append(record.getMessage()).append("\n");

            final Throwable throwable = record.getThrown();
            if(throwable != null) {
                final StackTraceElement[] trace = throwable.getStackTrace();
                for(StackTraceElement traceElement : trace) {
                    builder.append("\tat ").append(traceElement.toString()).append("\n");
                }
            }

            final Object[] params = record.getParameters();
            if(params != null)
            {
                builder.append("\t");
                for (int i = 0; i < params.length; i++)
                {
                    builder.append(params[i]);
                    if (i < params.length - 1)
                        builder.append(", ");
                }
                builder.append("\n");
            }

            builder.append(ANSI_RESET);
            return builder.toString();
        }

        private String calcDate(long ms) {
            Date resultDate = new Date(ms);
            return dateFormat.format(resultDate);
        }

        private String getColorFromLevel(Level level) {
            if(level == Level.SEVERE) {
                return ANSI_RED;
            }
            if(level == Level.WARNING) {
                return ANSI_YELLOW;
            }
            if(level == Level.INFO) {
                return ANSI_CYAN;
            }
            if(level == Level.CONFIG) {
                return ANSI_GREEN;
            }
            return ANSI_WHITE;
        }
    }
}
