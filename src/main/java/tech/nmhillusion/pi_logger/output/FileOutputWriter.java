package tech.nmhillusion.pi_logger.output;

import tech.nmhillusion.pi_logger.constant.AnsiColor;
import tech.nmhillusion.pi_logger.constant.StringConstant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class FileOutputWriter implements IOutputWriter {
    private static final int FLUSH_INTERVAL = 100;

    private String logFilePath;
    private PrintWriter writer;
    private int writeCount = 0;

    public synchronized void setOutputLogFile(String logFilePath) {
        if (logFilePath != null && logFilePath.equals(this.logFilePath) && writer != null) {
            return;
        }

        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                final boolean createdFile = logFile.createNewFile();
                if (!createdFile) {
                    throw new IOException("Cannot create file log: " + logFilePath);
                }
            }

            this.logFilePath = logFilePath;
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String removeColorCharsInMessage(String message) {
        for (String ansiColor : AnsiColor.colorValues) {
            message = message
                    .replace(ansiColor, StringConstant.EMPTY);
        }

        for (String ansiColor : AnsiColor.backgroundColorValues) {
            message = message
                    .replace(ansiColor, StringConstant.EMPTY);
        }

        return message;
    }

    @Override
    public synchronized void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        if (writer == null) {
            return;
        }

        writer.println(removeColorCharsInMessage(outputMessage));

        if (null != throwableList) {
            for (final Throwable throwable_ : throwableList) {
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