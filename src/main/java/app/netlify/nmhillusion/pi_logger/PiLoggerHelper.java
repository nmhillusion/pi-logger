package app.netlify.nmhillusion.pi_logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date: 2022-02-08
 * <p>
 * created-by: minguy1
 */

public class PiLoggerHelper {
    private static final Map<String, PiLogger> loggerFactory = new ConcurrentHashMap<>();

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
            final PiLogger piLogger = new PiLogger(loggerClass);
            loggerFactory.put(loggerKey, piLogger);

            return piLogger;
        }
    }

}
