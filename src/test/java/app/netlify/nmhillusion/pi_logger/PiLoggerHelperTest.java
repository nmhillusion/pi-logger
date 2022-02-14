package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class PiLoggerHelperTest {

    @BeforeAll
    static void init() {
        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
                .setLogLevel(LogLevel.ERROR)
        ;
    }


    @Test
    void testDebug() {
        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.DEBUG);

        PiLoggerHelper.getLog(this).debug("do test debug message");

        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerHelper.getLog(this).debug("will not log this message");
    }

    @Test
    void testInfo() {
        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerHelper.getLog(this).info("do test info message");

        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        PiLoggerHelper.getLog(this).info("will not log this message");
    }

    @Test
    void testWarn() {
        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.WARN);

        PiLoggerHelper.getLog(this).warn("do test warn message");

        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.ERROR);

        PiLoggerHelper.getLog(this).warn("will not log this message");
    }

    @Test
    void testError() {
        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.INFO);

        PiLoggerHelper.getLog(this).error("do test error message with exception: ", new SQLException("Random Fatal Error"));

        PiLoggerHelper.getLogConfig()
                .setLogLevel(LogLevel.ERROR);
        PiLoggerHelper.getLog(this).error("do test error message");
    }
}