package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RotationTest {
    private final String SIZE_LOG_FILE_PATH = "logs/test_rotation_size.log";
    private final String TIME_LOG_FILE_PATH = "logs/test_rotation_time.log";

    @BeforeEach
    void setup() throws IOException {
        cleanUp();
    }

    @AfterEach
    void tearDown() throws IOException {
        cleanUp();
    }

    private void cleanUp() throws IOException {
        for (String path : new String[]{SIZE_LOG_FILE_PATH, TIME_LOG_FILE_PATH}) {
            File logFile = new File(path);
            if (logFile.exists()) {
                logFile.delete();
            }

            File dir = new File(".");
            File[] rotatedFiles = dir.listFiles((d, name) -> name.startsWith(path + ".") && name.endsWith(".log"));
            if (rotatedFiles != null) {
                for (File f : rotatedFiles) {
                    f.delete();
                }
            }
        }
    }

    @Test
    void testSizeRotation() throws Exception {
        System.out.println("Starting testSizeRotation, user.dir=" + System.getProperty("user.dir"));
        PiLogger logger = PiLoggerFactory.getLogger(this);
        logger.getLogConfig()
                .setLogLevel(tech.nmhillusion.pi_logger.constant.LogLevel.INFO)
                .setIsOutputToFile(true)
                .setLogFilePath(SIZE_LOG_FILE_PATH)
                .setMaxFileSizeMB(1) // 1KB
                .setMaxBackupFiles(2);

        // Write enough data to trigger rotation (> 1KB)
        for (int i = 0; i < 50; i++) {
            logger.info("This is a test message to fill the log file. Message number: " + i);
        }

        // Wait for async logger to finish
        logger.flush().get();

        File dir = new File(".");
        File[] rotatedFiles = dir.listFiles((d, name) -> name.startsWith(SIZE_LOG_FILE_PATH + ".") && name.endsWith(".log"));
        
        assertTrue(rotatedFiles != null && rotatedFiles.length > 0, "Should have at least one rotated file");
    }

    @Test
    void testTimeRotation() throws Exception {
        PiLogger logger = PiLoggerFactory.getLogger((Object) "TimeRotationLogger");
        logger.getLogConfig()
                .setLogLevel(tech.nmhillusion.pi_logger.constant.LogLevel.INFO)
                .setIsOutputToFile(true)
                .setLogFilePath(TIME_LOG_FILE_PATH)
                .setMaxFileAgeDays(1)
                .setMaxBackupFiles(2);

        logger.info("Initial message");
        logger.flush().get(); // ensure file is written

        File logFile = new File(TIME_LOG_FILE_PATH);
        assertTrue(logFile.exists(), "Log file should exist: " + logFile.getAbsolutePath());

        // Simulate file being 3 days old to avoid edge cases with toDays()
        long threeDaysAgo = System.currentTimeMillis() - (3L * 24 * 60 * 60 * 1000);
        assertTrue(logFile.setLastModified(threeDaysAgo), "Should be able to set last modified");

        logger.info("Message after simulated 3 days");
        logger.flush().get();

        File dir = new File(".");
        File[] rotatedFiles = dir.listFiles((d, name) -> name.startsWith(TIME_LOG_FILE_PATH + ".") && name.endsWith(".log"));
        
        assertTrue(rotatedFiles != null && rotatedFiles.length > 0, "Should have rotated due to age");
    }
}
