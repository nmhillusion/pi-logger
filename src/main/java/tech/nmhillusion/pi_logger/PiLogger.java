package tech.nmhillusion.pi_logger;

import org.slf4j.Marker;
import tech.nmhillusion.pi_logger.constant.AnsiColor;
import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.constant.StringConstant;
import tech.nmhillusion.pi_logger.factory.PiLoggerFactory;
import tech.nmhillusion.pi_logger.model.LogConfigModel;
import tech.nmhillusion.pi_logger.output.CompositeRotationPolicy;
import tech.nmhillusion.pi_logger.output.ConsoleOutputWriter;
import tech.nmhillusion.pi_logger.output.FileOutputWriter;
import tech.nmhillusion.pi_logger.output.IOutputWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
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
    private final static ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(r -> {
        final Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("PiLogger-Thread");
        return thread;
    });
    private static final String NORMAL_TEMPLATE = getColorTemplate(false);
    private static final String COLOR_TEMPLATE = getColorTemplate(true);
    private static final ConsoleOutputWriter consoleOutputWriter = new ConsoleOutputWriter();
    private static final Pattern HAS_STRING_FORMAT_PATTERN = Pattern.compile("%[a-z]", Pattern.CASE_INSENSITIVE);
    private static final String ROTATE_TIMESTAMP_SIGNATURE = "yyyy-MM-dd_HH-mm-ss_SSS";
    private static final LogConfigModel logConfig = PiLoggerFactory.getLogConfig();
    private static final Queue<Future<?>> TASK_QUEUE = new java.util.concurrent.ConcurrentLinkedQueue<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(EXECUTOR_SERVICE::shutdown));
    }

    private final String loggerName;
    private final List<IOutputWriter> logOutputWriters = new CopyOnWriteArrayList<>();
    private final AtomicReference<String> TEMPLATE_REF = new AtomicReference<>();
    private DateTimeFormatter dateTimeFormatter;
    private FileOutputWriter fileOutputWriter;

    public PiLogger(String loggerName) {
        this.loggerName = loggerName;
        setLogConfig();
    }

    private static String getColorTemplate(boolean isColoring) {
        return placeholderAnsiCode(isColoring, AnsiColor.ANSI_CYAN) + "$TIMESTAMP" + placeholderAnsiCode(isColoring, AnsiColor.ANSI_RESET) +
                " -- " +
                placeholderAnsiCode(isColoring, "$ANSI_COLOR") + "[$LOG_LEVEL]" + placeholderAnsiCode(isColoring, AnsiColor.ANSI_RESET) +
                " -- " +
                "[$THREAD_NAME]" +
                " -- " +
                "[$PID]" +
                " -- " +
                placeholderAnsiCode(isColoring, AnsiColor.ANSI_PURPLE) + "$LOG_NAME$LINE_NUMBER" + placeholderAnsiCode(isColoring, AnsiColor.ANSI_RESET) +
                " : $LOG_MESSAGE";
    }

    private static String placeholderAnsiCode(boolean isColoring, String replacementAnsiCode) {
        return isColoring ? replacementAnsiCode : StringConstant.EMPTY;
    }

    public static LogConfigModel getLogConfig() {
        return logConfig;
    }

    public PiLogger setLogConfig() {
        addOutputWriter(consoleOutputWriter);
        this.registerOnChangeConfig(logConfig);

        logConfig.setOnChangeConfig(this::registerOnChangeConfig);
        return this;
    }

    private synchronized void registerOnChangeConfig(LogConfigModel newConfig) {
        this.forceFlush();

        this.dateTimeFormatter = DateTimeFormatter.ofPattern(newConfig.getTimestampPattern());
        TEMPLATE_REF.set(newConfig.getColoring() ? COLOR_TEMPLATE : NORMAL_TEMPLATE);

        if (logConfig.isOutputToFile()) {
            this.fileOutputWriter = FileOutputWriter.getSharedInstance(logConfig.getLogFilePath());
            fileOutputWriter.setOutputLogFile(logConfig.getLogFilePath());
            fileOutputWriter.setRotationPolicy(
                    new CompositeRotationPolicy.CompositeRotationPolicyConfig(
                            logConfig.getMaxFileSizeMB(),
                            logConfig.getMaxFileAgeDays(),
                            logConfig.getMaxBackupFiles(),
                            ROTATE_TIMESTAMP_SIGNATURE
                    )
            );

            if (!logOutputWriters.contains(fileOutputWriter)) {
                addOutputWriter(fileOutputWriter);
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

    private StackTraceElement getCallerFrameFromStackTrace(Thread currentThread) {
        final StackTraceElement[] stackTrace = currentThread.getStackTrace();
        StackTraceElement callerFrame = null;
        boolean foundSelf = false;
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (className.contains("$")) {
                className = className.substring(0, className.indexOf("$"));
            }

            if (className.equals(getClass().getName())) {
                foundSelf = true;
            } else if (foundSelf) {
                callerFrame = element;
                break;
            }
        }

        return callerFrame;
    }

    private void doLog(LogLevel logLevel, String messageFormat, Object... args) {
        final Thread currentThread = Thread.currentThread();
        final StackTraceElement finalCallerFrame = getCallerFrameFromStackTrace(currentThread);

        final Future<?> submittedTask = EXECUTOR_SERVICE.submit(() -> {
            doLogOnThread(currentThread, finalCallerFrame, logLevel, messageFormat, args);
        });

        addTaskToQueue(submittedTask);
    }

    private void addTaskToQueue(Future<?> submittedTask) {
        TASK_QUEUE.add(submittedTask);
        TASK_QUEUE.removeIf(Future::isDone);
    }

    private void flushQueue() {
        for (Future<?> future : TASK_QUEUE) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(System.out);
            }
        }
    }

    private void doLogOnThread(Thread currentThread, StackTraceElement callerFrame, LogLevel logLevel, String messageFormat, Object... args) {
        if (logLevel.getPriority() < logConfig.getLogLevel().getPriority()) {
            return; // not log this because user don't want to write log in this log level
        }

        try {
            final List<Throwable> throwableFromArgs = Arrays.stream(args)
                    .filter(Throwable.class::isInstance)
                    .map(Throwable.class::cast)
                    .collect(Collectors.toList());

            String lineNumberText = StringConstant.EMPTY;
            if (logConfig.isDisplayLineNumber() && null != callerFrame) {
                lineNumberText = ":" + callerFrame.getLineNumber();
            }

            String finalLogMessage = TEMPLATE_REF.get()
                    .replace("$TIMESTAMP", LocalDateTime.now().format(dateTimeFormatter))
                    .replace("$LOG_LEVEL", logLevel.getValue())
                    .replace("$THREAD_NAME", currentThread.getName())
                    .replace("$PID", String.valueOf(ProcessHandle.current().pid()))
                    .replace("$LOG_NAME", loggerName)
                    .replace("$LINE_NUMBER", lineNumberText)
                    .replace("$LOG_MESSAGE", buildLogMessage(messageFormat, args));

            if (logConfig.getColoring()) {
                finalLogMessage = finalLogMessage
                        .replace("$ANSI_COLOR", logLevel.getColor());
            }

            doWriteToOutputs(finalLogMessage, throwableFromArgs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String buildLogMessage(String messageFormat, Object[] args) {
        if (null == args || 0 == args.length) {
            return messageFormat;
        }

        if (HAS_STRING_FORMAT_PATTERN.matcher(messageFormat).find()) {
            return String.format(messageFormat, args);
        } else if (messageFormat.contains("{}")) {
            String finalMessage = messageFormat;
            for (Object arg : args) {
                finalMessage = finalMessage.replaceFirst("\\{}", String.valueOf(arg));
            }
            return finalMessage;
        } else {
            return messageFormat + " " +
                    Stream.of(args).map(String::valueOf).collect(Collectors.joining(" "));
        }
    }

    private void doWriteToOutputs(String logMessage, List<Throwable> throwableList) throws IOException {
        for (IOutputWriter outputWriter : logOutputWriters) {
            outputWriter.doOutput(logMessage, throwableList);
        }
    }

    public java.util.concurrent.Future<Void> flush() {
        flushQueue();
        return EXECUTOR_SERVICE.submit(() -> {
            _flush();
            return null;
        });
    }

    private void _flush() throws IOException {
        for (final IOutputWriter outputWriter : logOutputWriters) {
            outputWriter.flush();
        }
    }

    public void forceFlush() {
        try {
            flushQueue();
            _flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return loggerName;
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
        doLog(LogLevel.TRACE, logMessage, throwable);
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
        doLog(LogLevel.DEBUG, logMessage, throwable);
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
        doLog(LogLevel.INFO, logMessage, throwable);
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
        doLog(LogLevel.WARN, logMessage, throwable);
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
