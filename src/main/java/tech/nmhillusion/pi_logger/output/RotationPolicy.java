package tech.nmhillusion.pi_logger.output;

import java.io.File;

/**
 * Policy interface for determining when log rotation should occur.
 *
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public interface RotationPolicy {
    /**
     * Check if rotation should occur based on current file state.
     *
     * @param currentFile the current log file
     * @param fileSizeKB  current file size in KB
     * @return true if rotation should occur
     */
    boolean shouldRotate(File currentFile, long fileSizeKB);

    /**
     * Generate a rotated filename for the old log file.
     *
     * @param currentFile the current log file
     * @return the rotated filename (typically with timestamp appended)
     */
    String generateRotatedFilename(File currentFile);
}