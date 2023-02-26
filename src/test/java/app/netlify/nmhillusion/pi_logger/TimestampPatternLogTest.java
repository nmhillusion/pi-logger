package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class TimestampPatternLogTest {
    private final PiLogger logger = PiLoggerFactory.getLog(this);

    @Test
    void testTimestampPattern1() {
        final String timestampPattern = "dd-MM-yyyy";

        logger.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        logger.info(timestampPattern);
    }

    @Test
    void testTimestampPattern2() {
        final String timestampPattern = "yyyy/MMM/dd HH:mm:ss";

        logger.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        logger.info(timestampPattern);
    }

    @Test
    void testTimestampPattern3() {
        final String timestampPattern = "HH:mm:ss.SSSXXX";

        logger.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        logger.info(timestampPattern);
    }

}
