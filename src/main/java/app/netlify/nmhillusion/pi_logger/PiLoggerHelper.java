package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;
import app.netlify.nmhillusion.pi_logger.output.ConsoleOutputWriter;
import app.netlify.nmhillusion.pi_logger.output.FileOutputWriter;

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
    private static final ConsoleOutputWriter consoleOutputWriter = new ConsoleOutputWriter();
    private static final FileOutputWriter fileOutputWriter = new FileOutputWriter("output.log");

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
            piLogger.addOutputWriter(consoleOutputWriter);
            piLogger.addOutputWriter(fileOutputWriter);

            loggerFactory.put(loggerKey, piLogger);

            return piLogger;
        }
    }

}
