package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2026-04-05
 */
public class PenLogTest {
    private static final String EXAMPLE_LOG = """
            3 Body Problem is an American science fiction television series created by David Benioff, D. B. Weiss, and Alexander Woo. It is the third adaptation of the Chinese novel series Remembrance of Earth's Past by former computer engineer Liu Cixin. The series' title comes from the first volume, The Three-Body Problem, named after a classical physics problem dealing with Newton's laws of motion and gravitation. The eight-episode first season was released on Netflix on March 21, 2024.
            
            The series centers on a diverse group of characters, mainly scientists, who encounter an extraterrestrial civilization, triggering numerous threats and profound changes for humanity. While the two earlier adaptations, the animated The Three-Body Problem in Minecraft (2014–2020) and the live-action Three-Body (2023), were entirely in the novels' original Mandarin, 3 Body Problem is primarily in English with some Mandarin. It also alters parts of the original Chinese setting to include foreign characters and locations, particularly in the United Kingdom as well as China.
            
            This was Benioff and Weiss' first television project since the conclusion of their series Game of Thrones (2011–2019). It received positive reviews, with praise towards its cast, ambition, and production values. The series received six Primetime Emmy Award nominations, including Outstanding Drama Series. In May 2024, the series was renewed for a second and third season. The second season is set to premiere in 2026.
            """;
    private final PiLogger logger = PiLoggerFactory.getLogger(this);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void testPenLog() {
        logger.getLogConfig()
                .setColoring(true)
                .setIsOutputToFile(true)
        ;

        final String message = "test pen log %d. " + EXAMPLE_LOG;

        for (int i = 0; i < 20_000; i++) {
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

        final String message = "test pen log with multi thread[%s] %d. " + EXAMPLE_LOG;

        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            final Thread thread_ = new Thread(() -> {
                for (int j = 0; j < 1_000; j++) {
                    logger.info(message.formatted(Thread.currentThread().getName(), threadIndex * 1_000 + j));
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

        final String message = "test pen log with executor service[%s] %d. " + EXAMPLE_LOG;

        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                for (int j = 0; j < 1_000; j++) {
                    logger.info(message.formatted(Thread.currentThread().getName(), threadIndex * 1_000 + j));
                }
            });
        }

        Assertions.assertDoesNotThrow(() -> Thread.sleep(120_000));
    }
}
