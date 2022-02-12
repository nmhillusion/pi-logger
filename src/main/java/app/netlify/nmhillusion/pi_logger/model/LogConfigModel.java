package app.netlify.nmhillusion.pi_logger.model;

import app.netlify.nmhillusion.pi_logger.listener.OnChangeConfig;

import java.io.Serializable;

/**
 * date: 2022-02-11
 * <p>
 * created-by: nmhillusion
 */

public class LogConfigModel implements Serializable {
    private boolean coloring;
    private String timestampPattern;
    private boolean displayLineNumber;
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

    public boolean getDisplayLineNumber() {
        return displayLineNumber;
    }

    public LogConfigModel setDisplayLineNumber(boolean displayLineNumber) {
        this.displayLineNumber = displayLineNumber;
        triggerOnChangeConfig();

        return this;
    }

    public OnChangeConfig getOnChangeConfig() {
        return onChangeConfig;
    }

    public LogConfigModel setOnChangeConfig(OnChangeConfig onChangeConfig) {
        this.onChangeConfig = onChangeConfig;
        return this;
    }
}
