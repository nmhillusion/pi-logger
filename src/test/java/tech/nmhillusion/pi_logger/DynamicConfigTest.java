package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicConfigTest {

    @Test
    void testConfigIsolation() {
        PiLoggerFactory factory = new PiLoggerFactory();
        PiLogger logger1 = factory.getLogger("Logger1");
        PiLogger logger2 = factory.getLogger("Logger2");

        LogConfigModel config1 = logger1.getLogConfig();
        LogConfigModel config2 = logger2.getLogConfig();

        assertNotSame(config1, config2, "Each logger should have its own cloned config instance");

        config1.setLogLevel(LogLevel.ERROR);
        assertEquals(LogLevel.ERROR, logger1.getLogConfig().getLogLevel());
        assertNotEquals(LogLevel.ERROR, logger2.getLogConfig().getLogLevel(), "Changing logger1 config should not affect logger2");
    }

    @Test
    void testDefaultConfigPropagation() {
        PiLoggerFactory factory = new PiLoggerFactory();
        
        // Reset default to INFO
        PiLoggerFactory.getDefaultLogConfig().setLogLevel(LogLevel.INFO);
        
        PiLogger loggerBefore = factory.getLogger("Before");
        assertEquals(LogLevel.INFO, loggerBefore.getLogConfig().getLogLevel());

        // Change default to WARN
        PiLoggerFactory.getDefaultLogConfig().setLogLevel(LogLevel.WARN);

        PiLogger loggerAfter = factory.getLogger("After");
        assertEquals(LogLevel.WARN, loggerAfter.getLogConfig().getLogLevel(), "New loggers should inherit the new default config");
    }
}
