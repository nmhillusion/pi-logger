package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DynamicConfigTest {

    @Test
    void testConfigIsolation() {
        PiLoggerFactory factory = new PiLoggerFactory();
        PiLogger logger1 = factory.getLogger("Logger1");
        PiLogger logger2 = factory.getLogger("Logger2");

        LogConfigModel config1 = PiLogger.getLogConfig();
        LogConfigModel config2 = PiLogger.getLogConfig();

        assertSame(config1, config2, "Each logger should have the same config instance");

        config1.setLogLevel(LogLevel.ERROR);
        assertEquals(LogLevel.ERROR, PiLogger.getLogConfig().getLogLevel());
        assertEquals(LogLevel.ERROR, PiLogger.getLogConfig().getLogLevel(), "Changing logger1 config should affect logger2");
    }

    @Test
    void testDefaultConfigPropagation() {
        PiLoggerFactory factory = new PiLoggerFactory();
        LogLevel originalLevel = PiLoggerFactory.getDefaultLogConfig().getLogLevel();
        try {
            // Reset default to INFO
            PiLoggerFactory.getDefaultLogConfig().setLogLevel(LogLevel.INFO);

            PiLogger loggerBefore = factory.getLogger("Before");
            assertEquals(LogLevel.INFO, PiLogger.getLogConfig().getLogLevel());

            // Change default to WARN
            PiLogger.getLogConfig().setLogLevel(LogLevel.WARN);

            PiLogger loggerAfter = factory.getLogger("After");
            assertEquals(LogLevel.WARN, PiLogger.getLogConfig().getLogLevel(), "New loggers should inherit the new default config");
        } finally {
            PiLoggerFactory.getDefaultLogConfig().setLogLevel(originalLevel);
        }
    }
}
