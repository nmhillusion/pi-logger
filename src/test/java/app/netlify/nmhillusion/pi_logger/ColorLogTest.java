package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class ColorLogTest {

    @Test
    void testColoring() {
        PiLoggerHelper.getLogConfig()
                .setColoring(true)
        ;

        PiLoggerHelper.getLog(this).info("log with color");

        PiLoggerHelper.getLogConfig()
                .setColoring(false)
        ;
    }

    @Test
    void testWithoutColoring() {
        PiLoggerHelper.getLogConfig()
                .setColoring(false)
        ;

        PiLoggerHelper.getLog(this).info("log without color");
    }

}
