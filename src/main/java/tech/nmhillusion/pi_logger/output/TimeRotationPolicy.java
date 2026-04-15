package tech.nmhillusion.pi_logger.output;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Rotation policy based on file age.
 * Rotation occurs when the file hasn't been modified in maxAgeDays or more.
 *
 * date: 2026-04-12
 * <p>
 * created-by: nmhillusion
 */
public class TimeRotationPolicy implements RotationPolicy {
    private final int maxAgeDays;
    private final String timestampPattern;
    private volatile long lastRotationCheckTime;

    private long fileStartTime;

    public TimeRotationPolicy(int maxAgeDays) {
        this(maxAgeDays, "yyyy-MM-dd_HH-mm-ss_SSS");
    }

    public TimeRotationPolicy(int maxAgeDays, String timestampPattern) {
        this.maxAgeDays = maxAgeDays;
        this.timestampPattern = timestampPattern;
        this.lastRotationCheckTime = System.currentTimeMillis();
        this.fileStartTime = System.currentTimeMillis();
    }

    public void setFileStartTime(long fileStartTime) {
        this.fileStartTime = fileStartTime;
    }

    @Override
    public boolean shouldRotate(File currentFile) {
        if (maxAgeDays <= 0 || !currentFile.exists()) {
            return false;
        }

        long fileAgeMillis = System.currentTimeMillis() - fileStartTime;
        long fileAgeDays = TimeUnit.MILLISECONDS.toDays(fileAgeMillis);

        if (fileAgeDays >= maxAgeDays) {
            return true;
        }
        
        // Fallback to last modified if file was modified by something else or we lost state
        long lastModifiedAgeMillis = System.currentTimeMillis() - currentFile.lastModified();
        long lastModifiedAgeDays = TimeUnit.MILLISECONDS.toDays(lastModifiedAgeMillis);
        
        return lastModifiedAgeDays >= maxAgeDays;
    }

    @Override
    public String generateRotatedFilename(File currentFile) {
        String timestamp = new SimpleDateFormat(timestampPattern).format(new Date());
        return currentFile.getName() + "." + timestamp + ".log";
    }

    public long getLastRotationCheckTime() {
        return lastRotationCheckTime;
    }

    public void updateLastRotationCheckTime() {
        this.lastRotationCheckTime = System.currentTimeMillis();
    }
}