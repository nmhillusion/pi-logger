package tech.nmhillusion.pi_logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLoggerFactory implements ILoggerFactory {
    private static final Map<String, PiLogger> loggerFactory = new ConcurrentHashMap<>();
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String DEFAULT_LOG_FILE_PATH = "output.log";

    private static final LogConfigModel defaultLogConfig = new LogConfigModel()
            .setColoring(false)
            .setTimestampPattern(DEFAULT_DATE_PATTERN)
            .setLogFilePath(DEFAULT_LOG_FILE_PATH)
            .setIsOutputToFile(true)
            .setDisplayLineNumber(true)
            .setLogLevel(LogLevel.INFO);

    public static LogConfigModel getDefaultLogConfig() {
        return defaultLogConfig;
    }

    public static LogConfigModel getLogConfig() {
        return defaultLogConfig.clone();
    }

    public static PiLogger getLog(Object client) {
        String loggerName = PiLoggerFactory.class.getName();
        if (client instanceof Class) {
            loggerName = ((Class<?>) client).getName();
        } else if (client instanceof String) {
            loggerName = (String) client;
        } else if (null != client) {
            loggerName = client.getClass().getName();
        }

        return new PiLoggerFactory().getLogger(loggerName);
    }

    @Override
    public PiLogger getLogger(String name) {
        return loggerFactory.computeIfAbsent(name, key -> new PiLogger(key, getLogConfig()));
    }
}
