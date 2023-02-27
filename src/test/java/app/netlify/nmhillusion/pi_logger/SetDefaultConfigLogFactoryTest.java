package app.netlify.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;
import app.netlify.nmhillusion.pi_logger.PiLoggerFactory;
import app.netlify.nmhillusion.pi_logger.PiLogger;
import app.netlify.nmhillusion.pi_logger.constant.LogLevel;

/**
 * date: 2023-02-27
 * <p>
 * created-by: nmhillusion
 */

public class SetDefaultConfigLogFactoryTest {
  @BeforeAll
  static void init() {
    PiLoggerFactory.getDefaultLogConfig()
        .setColoring(true)
        .setDisplayLineNumber(false);
  }

  @Test
  void testConfigColorDisplayLineNumber() {
    final PiLogger logger = PiLoggerFactory.getLog(this);

    logger.info("test message for testConfigColorDisplayLineNumber");
  }

  @Test
  void testConfigColorDisplayLineNumberContinue() {
    final PiLogger logger = PiLoggerFactory.getLog(this);

    logger.info("test message for testConfigColorDisplayLineNumber 2");
  }
}
