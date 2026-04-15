package tech.nmhillusion.pi_logger.output;

import java.io.File;

/**
 * Rotation policy based on file size.
 * Rotation occurs when file size exceeds maxSizeKB.
 * <p>
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public class SizeRotationPolicy implements RotationPolicy {
    private final int maxSizeMB;
    private final String timestampPattern;

    public SizeRotationPolicy(int maxSizeMB) {
        this(maxSizeMB, "yyyy-MM-dd_HH-mm-ss_SSS");
    }

    public SizeRotationPolicy(int maxSizeMB, String timestampPattern) {
        this.maxSizeMB = maxSizeMB;
        this.timestampPattern = timestampPattern;
    }

    @Override
    public boolean shouldRotate(File currentFile) {
        final int fileSizeMB = Math.toIntExact(currentFile.length() / 1024 / 1024);

        if (maxSizeMB <= 0) {
            return false;
        }
        return fileSizeMB >= maxSizeMB;
    }

    @Override
    public String generateRotatedFilename(File currentFile) {
        String timestamp = new java.text.SimpleDateFormat(timestampPattern).format(new java.util.Date());
        return currentFile.getName() + "." + timestamp + ".log";
    }
}