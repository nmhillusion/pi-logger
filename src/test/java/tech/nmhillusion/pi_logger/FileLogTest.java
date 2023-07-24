package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class FileLogTest {
    private final PiLogger logger = PiLoggerFactory.getLog(this);

    @Test
    void testWriteFile() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(true)
        ;

        final String message = "write log to file";
        logger.info(message);

        final File logFile = new File(logger.getLogConfig().getLogFilePath());
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(logFile.toURI()));
            assertTrue(new String(bytes).contains(message), "Contains message in log file.");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    void testNoWriteFile() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(false)
        ;

        final String message = "don't write log to file";
        logger.info(message);

        final File logFile = new File(logger.getLogConfig().getLogFilePath());
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(logFile.toURI()));
            assertFalse(new String(bytes).contains(message), "Not contains message in log file.");
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
