package tech.nmhillusion.pi_logger.output;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Composite rotation policy that combines multiple policies.
 * Rotation occurs if ANY of the contained policies indicate rotation is needed.
 * <p>
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public class CompositeRotationPolicy implements RotationPolicy {
    private final SizeRotationPolicy sizePolicy;
    private final TimeRotationPolicy timePolicy;
    private final int maxBackupFiles;
    private final SimpleDateFormat timestampFormat;

    public CompositeRotationPolicy(CompositeRotationPolicyConfig property) {
        this.sizePolicy = new SizeRotationPolicy(property.maxFileSizeMB(), property.timestampPattern());
        this.timePolicy = new TimeRotationPolicy(property.maxFileAgeDays(), property.timestampPattern());
        this.maxBackupFiles = property.maxBackupFiles();
        this.timestampFormat = new SimpleDateFormat(property.timestampPattern());
    }

    @Override
    public boolean shouldRotate(File currentFile) {
        return sizePolicy.shouldRotate(currentFile) ||
                timePolicy.shouldRotate(currentFile);
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

    public record CompositeRotationPolicyConfig(int maxFileSizeMB, int maxFileAgeDays, int maxBackupFiles,
                                                String timestampPattern) {
    }
}