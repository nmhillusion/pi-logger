package tech.nmhillusion.pi_logger;

import org.slf4j.Marker;
import tech.nmhillusion.pi_logger.constant.AnsiColor;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.constant.StringConstant;
import tech.nmhillusion.pi_logger.model.LogConfigModel;
import tech.nmhillusion.pi_logger.output.ConsoleOutputWriter;
import tech.nmhillusion.pi_logger.output.FileOutputWriter;
import tech.nmhillusion.pi_logger.output.IOutputWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * date: 2022-02-08
 * <p>
 * created-by: nmhillusion
 */

public class PiLogger implements org.slf4j.Logger {
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final String NORMAL_TEMPLATE =
            "$TIMESTAMP -- [$LOG_LEVEL] -- [$THREAD_NAME] -- $LOG_NAME.$METHOD_NAME()$LINE_NUMBER : $LOG_MESSAGE";
    private static final String COLOR_TEMPLATE =
            AnsiColor.ANSI_CYAN + "$TIMESTAMP" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "$ANSI_COLOR[$LOG_LEVEL]" + AnsiColor.ANSI_RESET +
                    " -- " +
                    "[$THREAD_NAME]" +
                    " -- " +
                    "[$PID]" +
                    " -- " +
                    AnsiColor.ANSI_PURPLE + "$LOG_NAME.$METHOD_NAME()" + AnsiColor.ANSI_RESET +
                    "$LINE_NUMBER : $LOG_MESSAGE";
    private static final ConsoleOutputWriter consoleOutputWriter = new ConsoleOutputWriter();
    private static final FileOutputWriter fileOutputWriter = new FileOutputWriter();
    private static final Pattern HAS_STRING_FORMAT_PATTERN = Pattern.compile("%[a-z]", Pattern.CASE_INSENSITIVE);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat();
    private final Class<?> loggerClass;
    private final AtomicReference<String> TEMPLATE_REF = new AtomicReference<>();
    private final List<IOutputWriter> logOutputWriters = new ArrayList<>();
    private LogConfigModel logConfig;

    protected PiLogger(Class<?> loggerClass, LogConfigModel logConfig) {
        this.loggerClass = loggerClass;
        setLogConfig(logConfig);
    }

    public LogConfigModel getLogConfig() {
        return logConfig;
    }

    public PiLogger setLogConfig(LogConfigModel newConfig) {
        if (null != newConfig) {
            this.logConfig = newConfig;
            dateFormat.applyPattern(newConfig.getTimestampPattern());

            TEMPLATE_REF.set(logConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE);

            logOutputWriters.clear();
            logOutputWriters.add(consoleOutputWriter);
            if (logConfig.isOutputToFile()) {
                fileOutputWriter.setOutputLogFile(logConfig.getLogFilePath());
                logOutputWriters.add(fileOutputWriter);
            }

            this.logConfig.setOnChangeConfig(this::registerOnChangeConfig);
        }
        return this;
    }

    private void registerOnChangeConfig(LogConfigModel newConfig) {
        dateFormat.applyPattern(newConfig.getTimestampPattern());
        TEMPLATE_REF.set(newConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE);

        if (logConfig.isOutputToFile()) {
            fileOutputWriter.setOutputLogFile(logConfig.getLogFilePath());

            if (!logOutputWriters.contains(fileOutputWriter)) {
                logOutputWriters.add(fileOutputWriter);
            }
        } else {
            logOutputWriters.removeIf(FileOutputWriter.class::isInstance);
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

    private void doLog(LogLevel logLevel, String messageFormat, Object... args) {
        EXECUTOR_SERVICE.execute(() -> {
            doLogOnThread(logLevel, messageFormat, args);
        });
    }

    private void doLogOnThread(LogLevel logLevel, String messageFormat, Object... args) {
        if (logLevel.getPriority() < this.logConfig.getLogLevel().getPriority()) {
            return; // not log this because user don't want to write log in this log level
        }

        try {
            final StackTraceElement logStackTraceElement = getLogStackTraceElement();

            List<Throwable> throwableFromArgs = Arrays.stream(args)
                    .filter(Throwable.class::isInstance)
                    .map(Throwable.class::cast)
                    .collect(Collectors.toList());

            String finalLogMessage = TEMPLATE_REF.get()
                    .replace("$TIMESTAMP", dateFormat.format(Calendar.getInstance().getTime()))
                    .replace("$LOG_LEVEL", logLevel.getValue())
                    .replace("$THREAD_NAME", Thread.currentThread().getName())
                    .replace("$PID", String.valueOf(ProcessHandle.current().pid()))
                    .replace("$LOG_NAME", loggerClass.getName())
                    .replace("$METHOD_NAME", logStackTraceElement != null ? logStackTraceElement.getMethodName() : StringConstant.EMPTY)
                    .replace("$LOG_MESSAGE", buildLogMessage(messageFormat, args));

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

            doWriteToOutputs(finalLogMessage, throwableFromArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String buildLogMessage(String messageFormat, Object[] args) {
        if (HAS_STRING_FORMAT_PATTERN.matcher(messageFormat).find()) {
            return String.format(messageFormat, args);
        } else {
            return messageFormat +
                    Stream.of(args).map(String::valueOf).collect(Collectors.joining(" "));
        }
    }

    private void doWriteToOutputs(String logMessage, List<Throwable> throwableList) throws IOException {
        for (IOutputWriter outputWriter : logOutputWriters) {
            outputWriter.doOutput(logMessage, throwableList);
        }
    }

    @Override
    public String getName() {
        return loggerClass.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logConfig.getLogLevel().getPriority() <= LogLevel.TRACE.getPriority();
    }

    public void trace(String logMessage) {
        trace(logMessage, new Object[0]);
    }

    @Override
    public void trace(String format, Object arg) {
        trace(format, new Object[]{arg});
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        trace(format, new Object[]{arg1, arg2});
    }

    @Override
    public void trace(String messageFormat, Object... arguments) {
        doLog(LogLevel.TRACE, messageFormat, arguments);
    }

    public void trace(Throwable throwable) {
        trace(throwable.getMessage(), throwable);
    }

    public void trace(String logMessage, Throwable throwable) {
        trace(logMessage, new Object[]{throwable});
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
        trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        trace(format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        trace(format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logConfig.getLogLevel().getPriority() <= LogLevel.DEBUG.getPriority();
    }

    public void debug(String logMessage) {
        debug(logMessage, new Object[0]);
    }

    @Override
    public void debug(String format, Object arg) {
        debug(format, new Object[]{arg});
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        debug(format, new Object[]{arg1, arg2});
    }

    @Override
    public void debug(String format, Object... arguments) {
        doLog(LogLevel.DEBUG, format, arguments);
    }

    public void debug(Throwable throwable) {
        debug(throwable.getMessage(), throwable);
    }

    public void debug(String logMessage, Throwable throwable) {
        debug(logMessage, new Object[]{throwable});
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
        debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        debug(format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logConfig.getLogLevel().getPriority() <= LogLevel.INFO.getPriority();
    }

    public void info(String logMessage) {
        info(logMessage, new Object[0]);
    }

    @Override
    public void info(String format, Object arg) {
        info(format, new Object[]{arg});
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        info(format, new Object[]{arg1, arg2});
    }

    @Override
    public void info(String format, Object... arguments) {
        doLog(LogLevel.INFO, format, arguments);
    }

    public void info(Throwable throwable) {
        info(throwable.getMessage(), throwable);
    }

    public void info(String logMessage, Throwable throwable) {
        info(logMessage, new Object[]{throwable});
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logConfig.getLogLevel().getPriority() <= LogLevel.WARN.getPriority();
    }

    public void warn(String logMessage) {
        warn(logMessage, new Object[0]);
    }

    @Override
    public void warn(String format, Object arg) {
        warn(format, new Object[]{arg});
    }

    @Override
    public void warn(String format, Object... arguments) {
        doLog(LogLevel.WARN, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        warn(format, new Object[]{arg1, arg2});
    }

    public void warn(Throwable throwable) {
        warn(throwable.getMessage(), throwable);
    }

    public void warn(String logMessage, Throwable throwable) {
        warn(logMessage, new Object[]{throwable});
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logConfig.getLogLevel().getPriority() <= LogLevel.ERROR.getPriority();
    }

    public void error(String logMessage) {
        error(logMessage, new Object[0]);
    }

    @Override
    public void error(String format, Object arg) {
        error(format, new Object[]{arg});
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        error(format, new Object[]{arg1, arg2});
    }

    @Override
    public void error(String format, Object... arguments) {
        doLog(LogLevel.ERROR, format, arguments);
    }

    public void error(Throwable throwable) {
        error(throwable.getMessage(), throwable);
    }

    public void error(String logMessage, Throwable throwable) {
        doLog(LogLevel.ERROR, logMessage, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }
}
