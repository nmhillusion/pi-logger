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
        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(true);

        PiLoggerHelper.getLog(this).info("lineNumber");
    }

    @Test
    void testNoLineNumber() {
        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setDisplayLineNumber(false);

        PiLoggerHelper.getLog(this).info("noLineNumber");
    }
}
