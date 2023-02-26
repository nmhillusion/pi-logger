package app.netlify.nmhillusion.pi_logger.output;

import app.netlify.nmhillusion.pi_logger.constant.AnsiColor;
import app.netlify.nmhillusion.pi_logger.constant.StringConstant;

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

public class FileOutputWriter extends IOutputWriter {
    private FileOutputStream fileOutputStream;
    private PrintStream printStream;

    public void setOutputLogFile(String logFilePath) {
        try {
            final File logFile = new File(logFilePath);
            if (!logFile.exists()) {
                final boolean createdFile = logFile.createNewFile();

                if (!createdFile) {
                    throw new IOException("Cannot create file log: " + logFilePath);
                }
            }

            fileOutputStream = new FileOutputStream(logFilePath, true);
            printStream = new PrintStream(fileOutputStream, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected PrintStream getOutputPrintStream() {
        return printStream;
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

        super.doOutput(outputMessage, throwableList);

        if (null != fileOutputStream && null != throwableList) {
            for (final Throwable throwable_ : throwableList) {
                throwable_.printStackTrace(printStream);
            }
        }
    }
}
