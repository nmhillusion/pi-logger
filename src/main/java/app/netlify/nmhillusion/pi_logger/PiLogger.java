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

    private void doLog(LogLevel logLevel, String logMessage) {
        final String finalLogMessage = "$TIMESTAMP - [$LOG_LEVEL] - $LOG_NAME: $LOG_MESSAGE"
                .replace("$TIMESTAMP", dateFormat.format(Calendar.getInstance().getTime()))
                .replace("$LOG_LEVEL", logLevel.getValue())
                .replace("$LOG_NAME", loggerClass.getName())
                .replace("$LOG_MESSAGE", logMessage);

        System.out.println(finalLogMessage);
    }

    public void debug(String logMessage) {
        doLog(LogLevel.DEBUG, logMessage);
    }

    public void info(String logMessage) {
        doLog(LogLevel.INFO, logMessage);
    }

    public void warn(String logMessage) {
        doLog(LogLevel.WARN, logMessage);
    }

    public void error(String logMessage) {
        doLog(LogLevel.ERROR, logMessage);
    }

}
