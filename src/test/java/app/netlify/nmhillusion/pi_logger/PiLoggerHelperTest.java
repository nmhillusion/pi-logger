package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class PiLoggerHelperTest {

    @BeforeAll
    static void init() {
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .setLogLevel(LogLevel.ERROR)
        ;
    }


    @Test
    void testDebug() {
        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.DEBUG);

        PiLoggerFactory.getLog(this).debug("do test debug message");

        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerFactory.getLog(this).debug("will not log this message");
    }

    @Test
    void testInfo() {
        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerFactory.getLog(this).info("do test info message");

        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        PiLoggerFactory.getLog(this).info("will not log this message");
    }

    @Test
    void testWarn() {
        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        PiLoggerFactory.getLog(this).warn("do test warn message");

        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.ERROR);

        PiLoggerFactory.getLog(this).warn("will not log this message");
    }

    @Test
    void testError() {
        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerFactory.getLog(this).error("do test error message with exception: ", new SQLException("Random Fatal Error"));

        PiLoggerFactory.getLogConfig()
                .setLogLevel(LogLevel.ERROR);
        PiLoggerFactory.getLog(this).error("do test error message");
    }
}