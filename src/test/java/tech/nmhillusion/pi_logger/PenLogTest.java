package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-04-05
 */
public class PenLogTest {
    private final PiLogger logger = PiLoggerFactory.getLog(this);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void testPenLog() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(true)
        ;

        final String message = "test pen log %d";

        for (int i = 0; i < 1_000_000; i++) {
            logger.info(message.formatted(i));
        }

        Assertions.assertDoesNotThrow(() -> Thread.sleep(120_000));
    }

    @Test
    public void testPenLogWithMultiThread() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(true)
        ;

        final String message = "test pen log with multi thread[%s] %d";

        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            final Thread thread_ = new Thread(() -> {
                for (int j = 0; j < 100_000; j++) {
                    logger.info(message.formatted(Thread.currentThread().getName(), threadIndex * 100_000 + j));
                }
            });
            thread_.setDaemon(false);
            thread_.start();
        }

        Assertions.assertDoesNotThrow(() -> Thread.sleep(120_000));
    }

    @Test
    public void testPenLogWithExecutorService() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(true)
        ;

        final String message = "test pen log with multi thread[%s] %d";

        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                for (int j = 0; j < 100_000; j++) {
                    logger.info(message.formatted(Thread.currentThread().getName(), threadIndex * 100_000 + j));
                }
            });
        }

        Assertions.assertDoesNotThrow(() -> Thread.sleep(120_000));
    }
}
