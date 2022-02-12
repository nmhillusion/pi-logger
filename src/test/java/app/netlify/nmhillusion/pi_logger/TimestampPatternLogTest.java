package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class TimestampPatternLogTest {

    @Test
    void timestampPattern1Test(){
        final String timestampPattern = "dd-MM-yyyy";

        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerHelper.getLog(this).info(timestampPattern);
    }

    @Test
    void timestampPattern2Test(){
        final String timestampPattern = "yyyy/MMM/dd HH:mm:ss";

        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerHelper.getLog(this).info(timestampPattern);
    }

    @Test
    void timestampPattern3Test(){
        final String timestampPattern = "HH:mm:ss.SSSXXX";

        PiLoggerHelper.getLogConfig()
                .setColoring(true)
                .setTimestampPattern(timestampPattern);

        PiLoggerHelper.getLog(this).info(timestampPattern);
    }

}
