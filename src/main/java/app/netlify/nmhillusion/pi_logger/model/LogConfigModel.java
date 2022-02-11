package app.netlify.nmhillusion.pi_logger.model;

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

    public boolean getColoring() {
        return coloring;
    }

    public LogConfigModel setColoring(boolean coloring) {
        this.coloring = coloring;
        return this;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public LogConfigModel setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
        return this;
    }

    public boolean getDisplayLineNumber() {
        return displayLineNumber;
    }

    public LogConfigModel setDisplayLineNumber(boolean displayLineNumber) {
        this.displayLineNumber = displayLineNumber;
        return this;
    }
}
