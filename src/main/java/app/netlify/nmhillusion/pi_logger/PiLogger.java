package app.netlify.nmhillusion.pi_logger;

import app.netlify.nmhillusion.pi_logger.constant.AnsiColor;
import app.netlify.nmhillusion.pi_logger.constant.LogLevel;
import app.netlify.nmhillusion.pi_logger.constant.StringConstant;
import app.netlify.nmhillusion.pi_logger.model.LogConfigModel;
import app.netlify.nmhillusion.pi_logger.output.ConsoleOutputWriter;
import app.netlify.nmhillusion.pi_logger.output.FileOutputWriter;
import app.netlify.nmhillusion.pi_logger.output.IOutputWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLogger {
    private static final String NORMAL_TEMPLATE =
            "$TIMESTAMP -- [$LOG_LEVEL] -- [$THREAD_NAME] -- $LOG_NAME.$METHOD_NAME()$LINE_NUMBER : $LOG_MESSAGE";
    private static final String COLOR_TEMPLATE =
            AnsiColor.ANSI_CYAN + "$TIMESTAMP" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "$ANSI_COLOR[$LOG_LEVEL]" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "[$THREAD_NAME]" +
                    " -- " +
                    AnsiColor.ANSI_PURPLE + "$LOG_NAME.$METHOD_NAME()" + AnsiColor.ANSI_RESET +
                    "$LINE_NUMBER : $LOG_MESSAGE";
    private static final ConsoleOutputWriter consoleOutputWriter = new ConsoleOutputWriter();
    private static final FileOutputWriter fileOutputWriter = new FileOutputWriter();

    private final LogConfigModel logConfig;
    private final SimpleDateFormat dateFormat;
    private final Class<?> loggerClass;
    private final AtomicReference<String> TEMPLATE_REF = new AtomicReference<>();
    private final List<IOutputWriter> logOutputWriters = new ArrayList<>();

    protected PiLogger(Class<?> loggerClass, LogConfigModel logConfig) {
        this.loggerClass = loggerClass;
        this.dateFormat = new SimpleDateFormat(logConfig.getTimestampPattern());
        this.logConfig = logConfig;

        TEMPLATE_REF.set(logConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE);

        logOutputWriters.add(consoleOutputWriter);
        if (logConfig.getOutputToFile()) {
            fileOutputWriter.setOutputLogFile(logConfig.getLogFilePath());
            logOutputWriters.add(fileOutputWriter);
        }

        this.logConfig.setOnChangeConfig(this::registerOnChangeConfig);
    }

    private void registerOnChangeConfig(LogConfigModel newConfig) {
        dateFormat.applyPattern(newConfig.getTimestampPattern());
        TEMPLATE_REF.set(newConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE);

        if (logConfig.getOutputToFile()) {
            fileOutputWriter.setOutputLogFile(logConfig.getLogFilePath());

            if (!logOutputWriters.contains(fileOutputWriter)) {
                logOutputWriters.add(fileOutputWriter);
            }
        } else {
            logOutputWriters.removeIf(writer -> writer instanceof FileOutputWriter);
        }
    }

    public void addOutputWriter(IOutputWriter outputWriter) {
        if (null != outputWriter) {
            logOutputWriters.add(outputWriter);
        }
    }

    private StackTraceElement getLogStackTraceElement() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement logStackTraceElement = null;

        if (0 < stackTrace.length) {
            logStackTraceElement = stackTrace[0];
        }

        for (StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().equals(loggerClass.getName())) {
                logStackTraceElement = stackTraceElement;
                break;
            }
        }
        return logStackTraceElement;
    }

    private void doLog(LogLevel logLevel, String logMessage, Throwable throwable) {
        if (logLevel.getPriority() < this.logConfig.getLogLevel().getPriority()) {
            return; // not log this because user dont want to write log in this log level
        }

        try {
            final StackTraceElement logStackTraceElement = getLogStackTraceElement();

            String finalLogMessage = TEMPLATE_REF.get()
                    .replace("$TIMESTAMP", dateFormat.format(Calendar.getInstance().getTime()))
                    .replace("$LOG_LEVEL", logLevel.getValue())
                    .replace("$THREAD_NAME", Thread.currentThread().getName())
                    .replace("$LOG_NAME", loggerClass.getName())
                    .replace("$LOG_MESSAGE", logMessage)
                    .replace("$METHOD_NAME", logStackTraceElement != null ? logStackTraceElement.getMethodName() : StringConstant.EMPTY);

            if (logConfig.getColoring()) {
                finalLogMessage = finalLogMessage
                        .replace("$ANSI_COLOR", logLevel.getColor());
            }

            if (logConfig.getDisplayLineNumber()) {
                finalLogMessage = finalLogMessage
                        .replace("$LINE_NUMBER",
                                ":" + (logStackTraceElement != null ?
                                        logStackTraceElement.getLineNumber() : 0));
            } else {
                finalLogMessage = finalLogMessage
                        .replace("$LINE_NUMBER", StringConstant.EMPTY);
            }

            doWriteToOutputs(finalLogMessage, throwable);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doWriteToOutputs(String logMessage, Throwable throwable) throws IOException {
        for (IOutputWriter outputWriter : logOutputWriters) {
            outputWriter.doOutput(logMessage, throwable);
        }
    }

    public void trace(String logMessage) {
        trace(logMessage, null);
    }

    public void trace(Throwable throwable) {
        trace(throwable.getMessage(), throwable);
    }

    public void trace(String logMessage, Throwable throwable) {
        doLog(LogLevel.TRACE, logMessage, throwable);
    }

    public void debug(String logMessage) {
        debug(logMessage, null);
    }

    public void debug(Throwable throwable) {
        debug(throwable.getMessage(), throwable);
    }

    public void debug(String logMessage, Throwable throwable) {
        doLog(LogLevel.DEBUG, logMessage, throwable);
    }

    public void info(String logMessage) {
        info(logMessage, null);
    }

    public void info(Throwable throwable) {
        info(throwable.getMessage(), throwable);
    }

    public void info(String logMessage, Throwable throwable) {
        doLog(LogLevel.INFO, logMessage, throwable);
    }

    public void warn(String logMessage) {
        warn(logMessage, null);
    }

    public void warn(Throwable throwable) {
        warn(throwable.getMessage(), throwable);
    }

    public void warn(String logMessage, Throwable throwable) {
        doLog(LogLevel.WARN, logMessage, throwable);
    }

    public void error(String logMessage) {
        error(logMessage, null);
    }

    public void error(Throwable throwable) {
        error(throwable.getMessage(), throwable);
    }

    public void error(String logMessage, Throwable throwable) {
        doLog(LogLevel.ERROR, logMessage, throwable);
    }

}
