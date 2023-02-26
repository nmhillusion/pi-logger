package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class LineNumberLogTest {
    private final PiLogger logger = PiLoggerFactory.getLog(this);

    @Test
    void testLineNumber() {
        logger.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true);

        logger.info("lineNumber");
    }

    @Test
    void testNoLineNumber() {
        logger.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(false);

        logger.info("noLineNumber");
    }
}
