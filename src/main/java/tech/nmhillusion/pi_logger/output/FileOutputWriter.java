package tech.nmhillusion.pi_logger.output;

import tech.nmhillusion.pi_logger.constant.AnsiColor;
import tech.nmhillusion.pi_logger.constant.StringConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class FileOutputWriter implements IOutputWriter {
    private String logFilePath;

    public void setOutputLogFile(String logFilePath) {
        try {
            final File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                final boolean createdFile = logFile.createNewFile();

                if (!createdFile) {
                    throw new IOException("Cannot create file log: " + logFilePath);
                }
            }

            this.logFilePath = logFilePath;
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
    public void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        outputMessage = removeColorCharsInMessage(outputMessage);
        try (final FileOutputStream fileOutputStream = new FileOutputStream(logFilePath, true);
             final PrintStream printStream = new PrintStream(fileOutputStream, true)) {

            printStream.println(outputMessage);

            if (null != throwableList) {
                for (final Throwable throwable_ : throwableList) {
                    throwable_.printStackTrace(printStream);
                }
            }
        }
    }
}
