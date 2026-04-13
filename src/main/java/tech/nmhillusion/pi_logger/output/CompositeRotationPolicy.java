package tech.nmhillusion.pi_logger.output;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Composite rotation policy that combines multiple policies.
 * Rotation occurs if ANY of the contained policies indicate rotation is needed.
 *
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public class CompositeRotationPolicy implements RotationPolicy {
    private final SizeRotationPolicy sizePolicy;
    private final TimeRotationPolicy timePolicy;
    private final int maxBackupFiles;
    private final SimpleDateFormat timestampFormat;

    public CompositeRotationPolicy(long maxSizeKB, int maxAgeDays, int maxBackupFiles) {
        this(maxSizeKB, maxAgeDays, maxBackupFiles, "yyyy-MM-dd_HH-mm-ss_SSS");
    }

    public CompositeRotationPolicy(long maxSizeKB, int maxAgeDays, int maxBackupFiles, String timestampPattern) {
        this.sizePolicy = new SizeRotationPolicy(maxSizeKB, timestampPattern);
        this.timePolicy = new TimeRotationPolicy(maxAgeDays, timestampPattern);
        this.maxBackupFiles = maxBackupFiles;
        this.timestampFormat = new SimpleDateFormat(timestampPattern);
    }

    @Override
    public boolean shouldRotate(File currentFile, long fileSizeKB) {
        return sizePolicy.shouldRotate(currentFile, fileSizeKB) ||
               timePolicy.shouldRotate(currentFile, fileSizeKB);
    }

    @Override
    public String generateRotatedFilename(File currentFile) {
        String timestamp = timestampFormat.format(new Date());
        return currentFile.getName() + "." + timestamp + ".log";
    }

    public int getMaxBackupFiles() {
        return maxBackupFiles;
    }

    public SizeRotationPolicy getSizePolicy() {
        return sizePolicy;
    }

    public TimeRotationPolicy getTimePolicy() {
        return timePolicy;
    }
}