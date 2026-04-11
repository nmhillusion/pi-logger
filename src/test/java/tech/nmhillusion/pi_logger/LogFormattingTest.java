package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

public class LogFormattingTest {
    private final PiLogger logger = PiLoggerFactory.getLogger(this);

    @Test
    void testStringFormat() {
        logger.info("Testing String.format: %s, %d", "Hello", 123);
    }

    @Test
    void testSlf4jFormat() {
        logger.info("Testing SLF4J format: {}, {}", "World", 456);
    }

    @Test
    void testFallbackFormat() {
        logger.info("Testing Fallback:", "Param1", "Param2");
    }

    @Test
    void testMixedFormat() {
        // Should prioritize String.format if % is found
        logger.info("Testing Mixed: %s and {}", "Percent");
    }
}
