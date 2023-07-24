package tech.nmhillusion.pi_logger;

import tech.nmhillusion.pi_logger.constant.LogLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class PiLoggerHelperTest {
    private static final PiLogger logger = PiLoggerFactory.getLog(PiLoggerHelperTest.class);

    @BeforeAll
    static void init() {
        logger.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .setLogLevel(LogLevel.ERROR)
        ;
    }

    @Test
    void testTrace() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.TRACE);

        logger.trace("do test trace message");

        logger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.trace("will not log this message");
    }

    @Test
    void testDebug() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.DEBUG);

        logger.debug("do test debug message");

        logger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.debug("will not log this message");
    }

    @Test
    void testInfo() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.info("do test info message");

        logger.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        logger.info("will not log this message");
    }

    @Test
    void testWarn() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        logger.warn("do test warn message");

        logger.getLogConfig()
                .setLogLevel(LogLevel.ERROR);

        logger.warn("will not log this message");
    }

    @Test
    void testError() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.error("do test error message with exception: ", new SQLException("Random Fatal Error"));

        logger.getLogConfig()
                .setLogLevel(LogLevel.ERROR);
        logger.error("do test error message");
    }
}