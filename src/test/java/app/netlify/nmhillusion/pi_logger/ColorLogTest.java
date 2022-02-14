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
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
        ;

        PiLoggerFactory.getLog(this).info("log with color");

        PiLoggerFactory.getLogConfig()
                .setColoring(false)
        ;
    }

    @Test
    void testWithoutColoring() {
        PiLoggerFactory.getLogConfig()
                .setColoring(false)
        ;

        PiLoggerFactory.getLog(this).info("log without color");
    }

}
