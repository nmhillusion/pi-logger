package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class TimestampPatternLogTest {

    @Test
    void testTimestampPattern1() {
        final String timestampPattern = "dd-MM-yyyy";

        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerFactory.getLog(this).info(timestampPattern);
    }

    @Test
    void testTimestampPattern2() {
        final String timestampPattern = "yyyy/MMM/dd HH:mm:ss";

        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerFactory.getLog(this).info(timestampPattern);
    }

    @Test
    void testTimestampPattern3() {
        final String timestampPattern = "HH:mm:ss.SSSXXX";

        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerFactory.getLog(this).info(timestampPattern);
    }

}
