package tech.nmhillusion.pi_logger.output;

import tech.nmhillusion.pi_logger.constant.AnsiColor;
import tech.nmhillusion.pi_logger.constant.StringConstant;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File output writer with log rotation support.
 * Supports size-based and time-based rotation.
 * <p>
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class FileOutputWriter implements IOutputWriter {
    private static final int FLUSH_INTERVAL = 100;
    private static final java.util.Map<String, FileOutputWriter> INSTANCES = new java.util.concurrent.ConcurrentHashMap<>();

    private String logFilePath;
    private PrintWriter writer;
    private int writeCount = 0;

    private CompositeRotationPolicy rotationPolicy;
    private int maxBackupFiles = 10;

    private String parentDirectory;

    public FileOutputWriter() {
    }

    public static FileOutputWriter getSharedInstance(String logFilePath) {
        if (logFilePath == null) {
            return new FileOutputWriter();
        }
        return INSTANCES.computeIfAbsent(logFilePath, path -> new FileOutputWriter());
    }

    public synchronized void setOutputLogFile(String logFilePath) {
        final File logFile = new File(logFilePath);
        if (logFilePath != null && logFilePath.equals(this.logFilePath) && writer != null && logFile.exists()) {
            return;
        }

        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                final boolean createdDir = parentDir.mkdirs();
                if (!createdDir) {
                    throw new IOException("Cannot create directory: " + parentDir.getAbsolutePath());
                }
            }

            if (!logFile.exists()) {
                final boolean createdFile = logFile.createNewFile();
                if (!createdFile) {
                    throw new IOException("Cannot create file log: " + logFilePath);
                }
            }

            this.logFilePath = logFilePath;
            this.parentDirectory = logFile.getParent();
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), false);

            updatePolicyFileState();

            // Register this instance if it has a logFilePath
            if (this.logFilePath != null) {
                INSTANCES.put(this.logFilePath, this);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updatePolicyFileState() {
        if (rotationPolicy == null) {
            return;
        }
        if (rotationPolicy.getTimePolicy() == null) {
            return;
        }
        if (logFilePath == null) {
            return;
        }
        if (!new File(logFilePath).exists()) {
            // If the file doesn't exist yet, its last modified time is not relevant.
            return;
        }

        rotationPolicy.getTimePolicy().setFileStartTime(new File(logFilePath).lastModified());
    }

    /**
     *
     * @param rotationPolicy
     */
    public synchronized void setRotationPolicy(CompositeRotationPolicy.CompositeRotationPolicyConfig rotationPolicy) {
        this.maxBackupFiles = rotationPolicy.maxBackupFiles();
        this.rotationPolicy = new CompositeRotationPolicy(rotationPolicy);
        updatePolicyFileState();
    }

    /**
     * Check and perform rotation if needed.
     */
    private synchronized void checkRotation() {
        if (rotationPolicy == null || logFilePath == null) {
            return;
        }

        final File currentFile = new File(logFilePath);
        if (!currentFile.exists()) {
            return;
        }

        if (rotationPolicy.shouldRotate(currentFile)) {
            rotate(currentFile);
        }
    }

    /**
     * Perform log rotation.
     * Closes current file, renames it with timestamp, creates new file.
     */
    private synchronized void rotate(File currentFile) {
        try {
            // Close current writer
            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }

            // Generate rotated filename
            String rotatedName = rotationPolicy.generateRotatedFilename(currentFile);
            File rotatedFile = parentDirectory != null ? new File(parentDirectory, rotatedName) : new File(rotatedName);

            // Rename current file to rotated name
            try {
                java.nio.file.Files.move(currentFile.toPath(), rotatedFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Delete old rotated files if exceeds maxBackupFiles
                cleanupOldRotatedFiles();

                // Create new writer for fresh log file
                if (!currentFile.exists()) {
                    currentFile.createNewFile();
                }
                writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), false);

                updatePolicyFileState();
            } catch (IOException moveEx) {
                // If move failed, try to reopen in append mode
                writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete old rotated files exceeding maxBackupFiles limit.
     */
    private void cleanupOldRotatedFiles() {
        if (maxBackupFiles <= 0) {
            return;
        }

        File dir = new File(parentDirectory != null ? parentDirectory : ".");
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        String baseName = new File(logFilePath).getName();
        File[] rotatedFiles = dir.listFiles((d, name) -> name.startsWith(baseName + ".") && name.endsWith(".log"));

        if (rotatedFiles == null || rotatedFiles.length <= maxBackupFiles) {
            return;
        }

        // Sort by last modified (oldest first)
        List<File> sortedFiles = Arrays.stream(rotatedFiles)
                .sorted(Comparator.comparingLong(File::lastModified))
                .collect(Collectors.toList());

        // Delete oldest files to keep only maxBackupFiles
        int filesToDelete = sortedFiles.size() - maxBackupFiles;
        for (int i = 0; i < filesToDelete; i++) {
            sortedFiles.get(i).delete();
        }
    }

    private String removeColorCharsInMessage(String message) {
        for (String ansiColor : AnsiColor.colorValues) {
            message = message.replace(ansiColor, StringConstant.EMPTY);
        }

        for (String ansiColor : AnsiColor.backgroundColorValues) {
            message = message.replace(ansiColor, StringConstant.EMPTY);
        }

        return message;
    }

    @Override
    public synchronized void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        if (writer == null) {
            return;
        }

        // Check rotation before writing
        checkRotation();

        String messageToPrint = removeColorCharsInMessage(outputMessage);
        writer.println(messageToPrint);

        if (null != throwableList) {
            for (final Throwable throwable_ : throwableList) {
                // Tracking size for stack traces is harder, but we can approximate or flush
                throwable_.printStackTrace(writer);
            }
        }

        writeCount++;
        if (writeCount >= FLUSH_INTERVAL) {
            writer.flush();
            writeCount = 0;
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        if (writer != null) {
            writer.flush();
            writeCount = 0;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (writer != null) {
            writer.flush();
            writer.close();
            writer = null;
            writeCount = 0;
        }
    }
}