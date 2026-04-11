package tech.nmhillusion.pi_logger;

import org.junit.jupiter.api.Test;
import tech.nmhillusion.pi_logger.model.LogConfigModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigSubstitutionTest {

    @Test
    void testEnvVarSubstitution() {
        LogConfigModel config = new LogConfigModel();
        
        // Use a common env var like PATH or OS
        String osName = System.getenv("OS");
        if (osName == null) {
            osName = "Windows_NT"; // Fallback for testing if OS is not set
        }
        
        config.setLogFilePath("logs/%OS%/app.log");
        
        // The implementation replaces %OS% or %os%
        assertTrue(config.getLogFilePath().contains(osName), "Log path should contain the OS name substituted from environment variables");
    }
}
