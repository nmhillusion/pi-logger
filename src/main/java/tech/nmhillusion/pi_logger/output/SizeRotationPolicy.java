package tech.nmhillusion.pi_logger.output;

import java.io.File;

/**
 * Rotation policy based on file size.
 * Rotation occurs when file size exceeds maxSizeKB.
 *
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public class SizeRotationPolicy implements RotationPolicy {
    private final long maxSizeKB;
    private final String timestampPattern;

    public SizeRotationPolicy(long maxSizeKB) {
        this(maxSizeKB, "yyyy-MM-dd_HH-mm-ss_SSS");
    }

    public SizeRotationPolicy(long maxSizeKB, String timestampPattern) {
        this.maxSizeKB = maxSizeKB;
        this.timestampPattern = timestampPattern;
    }

    @Override
    public boolean shouldRotate(File currentFile, long fileSizeKB) {
        if (maxSizeKB <= 0) {
            return false;
        }
        return fileSizeKB >= maxSizeKB;
    }

    @Override
    public String generateRotatedFilename(File currentFile) {
        String timestamp = new java.text.SimpleDateFormat(timestampPattern).format(new java.util.Date());
        return currentFile.getName() + "." + timestamp + ".log";
    }
}