package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLoggerHelper {
    private static final Map<String, PiLogger> loggerFactory = new ConcurrentHashMap<>();
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final LogConfigModel logConfig = new LogConfigModel()
            .setColoring(false)
            .setDisplayLineNumber(true)
            .setTimestampPattern(DEFAULT_DATE_PATTERN);

    public static LogConfigModel getLogConfig() {
        return logConfig;
    }

    public static PiLogger getLog(Object client) {
        Class<?> loggerClass = PiLoggerHelper.class;
        if (client instanceof Class) {
            loggerClass = (Class<?>) client;
        } else if (null != client) {
            loggerClass = client.getClass();
        }

        final String loggerKey = loggerClass.getName();
        if (loggerFactory.containsKey(loggerKey)) {
            return loggerFactory.get(loggerKey);
        } else {
            final PiLogger piLogger = new PiLogger(loggerClass, logConfig);
            loggerFactory.put(loggerKey, piLogger);

            return piLogger;
        }
    }

}
