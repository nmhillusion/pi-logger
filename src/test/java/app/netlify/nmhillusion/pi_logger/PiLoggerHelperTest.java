package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PiLoggerHelperTest {

    @BeforeAll
    static void init() {
        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true)
                .setTimestampPattern("yyyy-MM-dd HH:mm:ss.SSS")
        ;
    }


    @Test
    void testDebug() {
        PiLoggerHelper.getLog(this).debug("do test debug message");
    }

    @Test
    void testInfo() {
        PiLoggerHelper.getLog(this).info("do test info message");
    }

    @Test
    void testWarn() {
        PiLoggerHelper.getLog(this).warn("do test warn message");
    }

    @Test
    void testError() {
        PiLoggerHelper.getLog(this).error("do test error message");
        PiLoggerHelper.getLog(this).error("do test error message with exception: ", new SQLException("Random Fatal Error"));
    }
}