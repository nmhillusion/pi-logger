package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Slf4jIntegrationTest {

    @Test
    public void testSlf4jBinding() {
        Logger logger = LoggerFactory.getLogger(Slf4jIntegrationTest.class);
        System.out.println("Logger class: " + logger.getClass().getName());
        assertInstanceOf(PiLogger.class, logger, "Logger should be an instance of PiLogger");
        logger.info("Hello from SLF4J integration!");
    }
}
