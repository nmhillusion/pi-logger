package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

import java.sql.SQLException;

class PiLoggerFactoryTest {
    private static final PiLogger logger = PiLoggerFactory.getLogger(PiLoggerFactoryTest.class);

    @BeforeAll
    static void init() {
        logger.getLogConfig()
                .setColoring(true)
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

    @Test
    void testDisplayLineNumber() {
        logger.getLogConfig()
                .setLogLevel(LogLevel.INFO)
                .setDisplayLineNumber(true);

        logger.info("this message should have line number");

        logger.getLogConfig()
                .setDisplayLineNumber(false);

        logger.info("this message should NOT have line number");
    }
}
