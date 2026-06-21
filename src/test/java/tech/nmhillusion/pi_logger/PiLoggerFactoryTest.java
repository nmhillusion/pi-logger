package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

import java.sql.SQLException;

class PiLoggerFactoryTest {
    private static final PiLogger logger = PiLoggerFactory.getLogger(PiLoggerFactoryTest.class);

    @BeforeAll
    static void init() {
        PiLogger.getLogConfig()
                .setColoring(true)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .setLogLevel(LogLevel.ERROR)
        ;
    }

    @AfterEach
    void afterEach() {
        logger.flush();
    }

    @Test
    void testTrace() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.TRACE);

        logger.trace("do test trace message");

        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.trace("will not log this message");
    }

    @Test
    void testDebug() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.DEBUG);

        logger.debug("do test debug message");

        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.debug("will not log this message");
    }

    @Test
    void testInfo() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.info("do test info message");

        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        logger.info("will not log this message");
    }

    @Test
    void testWarn() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        logger.warn("do test warn message");

        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.ERROR);

        logger.warn("will not log this message");
    }

    @Test
    void testError() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        logger.error("do test error message with exception: ", new SQLException("Random Fatal Error"));

        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.ERROR);
        logger.error("do test error message");
    }

    @Test
    void testDisplayLineNumber() {
        PiLogger.getLogConfig()
                .setLogLevel(LogLevel.INFO)
                .setDisplayLineNumber(true);

        logger.info("this message should have line number");

//        PiLogger.getLogConfig()
//                .setDisplayLineNumber(false);

        logger.info("this message should NOT have line number");
    }
}
