package tech.nmhillusion.pi_logger.model;

import tech.nmhillusion.pi_logger.constant.LogLevel;
import tech.nmhillusion.pi_logger.listener.OnChangeConfig;

import java.io.Serializable;
import java.util.Map;

/**
 * date: 2022-02-11
 * <p>
 * created-by: nmhillusion
 */

public class LogConfigModel implements Serializable, Cloneable {
    private boolean coloring;
    private String timestampPattern;
    private LogLevel logLevel;

    private boolean isOutputToFile = false;
    private String logFilePath;

    private OnChangeConfig onChangeConfig;

    private void triggerOnChangeConfig() {
        if (null != onChangeConfig) {
            onChangeConfig.onChange(this);
        }
    }

    public boolean getColoring() {
        return coloring;
    }

    public LogConfigModel setColoring(boolean coloring) {
        this.coloring = coloring;
        triggerOnChangeConfig();

        return this;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public LogConfigModel setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
        triggerOnChangeConfig();

        return this;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public LogConfigModel setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        triggerOnChangeConfig();
        return this;
    }

    public OnChangeConfig getOnChangeConfig() {
        return onChangeConfig;
    }

    public LogConfigModel setOnChangeConfig(OnChangeConfig onChangeConfig) {
        this.onChangeConfig = onChangeConfig;
        triggerOnChangeConfig();
        return this;
    }

    public boolean isOutputToFile() {
        return isOutputToFile;
    }

    public LogConfigModel setIsOutputToFile(boolean outputToFile) {
        this.isOutputToFile = outputToFile;
        triggerOnChangeConfig();
        return this;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public LogConfigModel setLogFilePath(String logFilePath) {
        if (null != logFilePath) {
            final Map<String, String> environments = System.getenv();
            for (String envKey : environments.keySet()) {
                logFilePath = logFilePath
                        .replace("%" + envKey.toLowerCase() + "%", environments.get(envKey))
                        .replace("%" + envKey.toUpperCase() + "%", environments.get(envKey));
            }

        }

        this.logFilePath = logFilePath;
        triggerOnChangeConfig();
        return this;
    }

    @Override
    public LogConfigModel clone() {
        try {
            final LogConfigModel clone = (LogConfigModel) super.clone();
            clone.setColoring(coloring)
                    .setLogFilePath(logFilePath)
                    .setLogLevel(logLevel)
                    .setOnChangeConfig(onChangeConfig)
                    .setIsOutputToFile(isOutputToFile)
                    .setTimestampPattern(timestampPattern)
            ;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
