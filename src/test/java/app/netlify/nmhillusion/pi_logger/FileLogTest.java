package app.netlify.nmhillusion.pi_logger;

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
    @Test
    void testWriteFile() {
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setOutputToFile(true)
        ;

        final String message = "write log to file";
        PiLoggerFactory.getLog(this).info(message);

        final File logFile = new File(PiLoggerFactory.getLogConfig().getLogFilePath());
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(logFile.toURI()));
            assertTrue(new String(bytes).contains(message), "Contains message in log file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testNoWriteFile() {
        PiLoggerFactory.getLogConfig()
                .setColoring(true)
                .setOutputToFile(false)
        ;

        final String message = "don't write log to file";
        PiLoggerFactory.getLog(this).info(message);

        final File logFile = new File(PiLoggerFactory.getLogConfig().getLogFilePath());
        try {
            final byte[] bytes = Files.readAllBytes(Paths.get(logFile.toURI()));
            assertFalse(new String(bytes).contains(message), "Not contains message in log file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
