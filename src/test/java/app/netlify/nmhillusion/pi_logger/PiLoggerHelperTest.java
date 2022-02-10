package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PiLoggerHelperTest {

    @Test
    void testDebug() {
        PiLoggerHelper.getLog(this).debug("do test debug message");
        assertTrue(true, "OK");
    }

    @Test
    void testInfo() {
        PiLoggerHelper.getLog(this).info("do test info message");
        assertTrue(true, "OK");
    }

    @Test
    void testWarn() {
        PiLoggerHelper.getLog(this).warn("do test warn message");
        assertTrue(true, "OK");
    }

    @Test
    void testError() {
        PiLoggerHelper.getLog(this).error("do test error message");
        PiLoggerHelper.getLog(this).error("do test error message with exception: ", new SQLException("Random Fatal Error"));
        assertTrue(true, "OK");
    }

}