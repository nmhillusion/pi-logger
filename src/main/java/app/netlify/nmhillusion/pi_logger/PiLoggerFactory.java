package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLoggerFactory {
    private static final Map<String, PiLogger> loggerFactory = new ConcurrentHashMap<>();
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String DEFAULT_LOG_FILE_PATH = "output.log";

    private static final LogConfigModel defaultLogConfig = new LogConfigModel()
            .setColoring(false)
            .setDisplayLineNumber(true)
            .setTimestampPattern(DEFAULT_DATE_PATTERN)
            .setLogFilePath(DEFAULT_LOG_FILE_PATH)
            .setOutputToFile(false)
            .setLogLevel(LogLevel.INFO);

    public static void setDefaultLogConfig(LogConfigModel logConfigModel) {
        defaultLogConfig.setColoring(logConfigModel.getColoring())
                .setDisplayLineNumber(logConfigModel.getDisplayLineNumber())
                .setLogFilePath(logConfigModel.getLogFilePath())
                .setLogLevel(logConfigModel.getLogLevel())
                .setOnChangeConfig(logConfigModel.getOnChangeConfig())
                .setOutputToFile(logConfigModel.getOutputToFile())
                .setTimestampPattern(logConfigModel.getTimestampPattern())
        ;
    }

    public static LogConfigModel getLogConfig() {
        return defaultLogConfig.clone();
    }

    public static PiLogger getLog(Object client) {
        Class<?> loggerClass = PiLoggerFactory.class;
        if (client instanceof Class) {
            loggerClass = (Class<?>) client;
        } else if (null != client) {
            loggerClass = client.getClass();
        }

        final String loggerKey = loggerClass.getName();
        if (loggerFactory.containsKey(loggerKey)) {
            return loggerFactory.get(loggerKey);
        } else {
            final PiLogger piLogger = new PiLogger(loggerClass, getLogConfig());
            loggerFactory.put(loggerKey, piLogger);

            return piLogger;
        }
    }

}
