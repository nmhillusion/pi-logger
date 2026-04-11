package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class ColorLogTest {
    private final PiLogger logger = PiLoggerFactory.getLogger(this);

    @Test
    void testColoring() {
        logger.getLogConfig()
                .setColoring(true)
        ;

        logger.info("log with color");
    }

    @Test
    void testWithoutColoring() {
        logger.getLogConfig()
                .setColoring(false)
        ;

        logger.info("log without color");
    }

}
