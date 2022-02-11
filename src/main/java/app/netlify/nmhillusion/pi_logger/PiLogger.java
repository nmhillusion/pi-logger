package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.AnsiColor;
import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLogger {
    private static final String NORMAL_TEMPLATE =
            "$TIMESTAMP -- [$LOG_LEVEL] -- [$THREAD_NAME] -- $LOG_NAME.$METHOD_NAME()$LINE_NUMBER : $LOG_MESSAGE";
    private static final String COLOR_TEMPLATE =
            AnsiColor.ANSI_CYAN + "$TIMESTAMP" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "$ANSI_COLOR[$LOG_LEVEL]" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "[$THREAD_NAME]" +
                    " -- " +
                    AnsiColor.ANSI_PURPLE + "$LOG_NAME.$METHOD_NAME()" + AnsiColor.ANSI_RESET +
                    "$LINE_NUMBER : $LOG_MESSAGE";

    private final LogConfigModel logConfig;
    private final DateFormat dateFormat;
    private final Class<?> loggerClass;
    private final String TEMPLATE;

    public PiLogger(Class<?> loggerClass, LogConfigModel logConfig) {
        this.loggerClass = loggerClass;
        this.logConfig = logConfig;
        this.dateFormat = new SimpleDateFormat(logConfig.getTimestampPattern());

        TEMPLATE = logConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE;
    }

    private StackTraceElement getLogStackTraceElement() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement logStackTraceElement = null;

        if (0 < stackTrace.length) {
            logStackTraceElement = stackTrace[0];
        }

        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().equals(loggerClass.getName())) {
                logStackTraceElement = stackTraceElement;
                break;
            }
        }
        return logStackTraceElement;
    }

    private void doLog(LogLevel logLevel, String logMessage, Throwable throwable) {
        final StackTraceElement logStackTraceElement = getLogStackTraceElement();

        String finalLogMessage = TEMPLATE
                .replace("$TIMESTAMP", dateFormat.format(Calendar.getInstance().getTime()))
                .replace("$LOG_LEVEL", logLevel.getValue())
                .replace("$THREAD_NAME", Thread.currentThread().getName())
                .replace("$LOG_NAME", loggerClass.getName())
                .replace("$LOG_MESSAGE", logMessage)
                .replace("$METHOD_NAME", logStackTraceElement != null ? logStackTraceElement.getMethodName() : "");

        if (logConfig.getColoring()) {
            finalLogMessage = finalLogMessage
                    .replace("$ANSI_COLOR", logLevel.getColor());
        }

        if (logConfig.getDisplayLineNumber()) {
            finalLogMessage = finalLogMessage
                    .replace("$LINE_NUMBER",
                            ":" + (logStackTraceElement != null ?
                                    logStackTraceElement.getLineNumber() : 0));
        } else {
            finalLogMessage = finalLogMessage
                    .replace("$LINE_NUMBER", "");
        }

        System.out.println(finalLogMessage);

        if (null != throwable) {
            throwable.printStackTrace();
        }
    }

    public void debug(String logMessage) {
        debug(logMessage, null);
    }

    public void debug(Throwable throwable) {
        debug(throwable.getMessage(), throwable);
    }

    public void debug(String logMessage, Throwable throwable) {
        doLog(LogLevel.DEBUG, logMessage, throwable);
    }

    public void info(String logMessage) {
        info(logMessage, null);
    }

    public void info(Throwable throwable) {
        info(throwable.getMessage(), throwable);
    }

    public void info(String logMessage, Throwable throwable) {
        doLog(LogLevel.INFO, logMessage, throwable);
    }

    public void warn(String logMessage) {
        warn(logMessage, null);
    }

    public void warn(Throwable throwable) {
        warn(throwable.getMessage(), throwable);
    }

    public void warn(String logMessage, Throwable throwable) {
        doLog(LogLevel.WARN, logMessage, throwable);
    }

    public void error(String logMessage) {
        error(logMessage, null);
    }

    public void error(Throwable throwable) {
        error(throwable.getMessage(), throwable);
    }

    public void error(String logMessage, Throwable throwable) {
        doLog(LogLevel.ERROR, logMessage, throwable);
    }

}
