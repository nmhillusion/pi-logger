package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.LogLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * date: 2022-02-08
 * <p>
 * created-by: minguy1
 */

public class PiLogger {
    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private final static DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    private final Class<?> loggerClass;

    public PiLogger(Class<?> loggerClass) {
        this.loggerClass = loggerClass;
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

        final String finalLogMessage = "$TIMESTAMP -- [$LOG_LEVEL] -- [$THREAD_NAME] -- $LOG_NAME.$METHOD_NAME():$LINE_NUMBER : $LOG_MESSAGE"
                .replace("$TIMESTAMP", dateFormat.format(Calendar.getInstance().getTime()))
                .replace("$LOG_LEVEL", logLevel.getValue())
                .replace("$THREAD_NAME", Thread.currentThread().getName())
                .replace("$LOG_NAME", loggerClass.getName())
                .replace("$LOG_MESSAGE", logMessage)
                .replace("$METHOD_NAME", logStackTraceElement != null ? logStackTraceElement.getMethodName() : "")
                .replace("$LINE_NUMBER", String.valueOf(logStackTraceElement != null ? logStackTraceElement.getLineNumber() : 0));

        System.out.println(finalLogMessage);

        if (null != throwable) {
            throwable.printStackTrace();
        }
    }

    public void debug(String logMessage) {
        debug(logMessage, null);
    }

    public void debug(String logMessage, Throwable throwable) {
        doLog(LogLevel.DEBUG, logMessage, throwable);
    }

    public void info(String logMessage) {
        info(logMessage, null);
    }

    public void info(String logMessage, Throwable throwable) {
        doLog(LogLevel.INFO, logMessage, throwable);
    }

    public void warn(String logMessage) {
        warn(logMessage, null);
    }

    public void warn(String logMessage, Throwable throwable) {
        doLog(LogLevel.WARN, logMessage, throwable);
    }

    public void error(String logMessage) {
        error(logMessage, null);
    }

    public void error(String logMessage, Throwable throwable) {
        doLog(LogLevel.ERROR, logMessage, throwable);
    }

}
