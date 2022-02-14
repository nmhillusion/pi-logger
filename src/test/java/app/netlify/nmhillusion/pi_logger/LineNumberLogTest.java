package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class LineNumberLogTest {
    @Test
    void testLineNumber() {
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true);

        PiLoggerFactory.getLog(this).info("lineNumber");
    }

    @Test
    void testNoLineNumber() {
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(false);

        PiLoggerFactory.getLog(this).info("noLineNumber");
    }
}
