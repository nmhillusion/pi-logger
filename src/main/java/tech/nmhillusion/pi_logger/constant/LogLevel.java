package tech.nmhillusion.pi_logger.constant;

import org.slf4j.event.Level;

/**
 * date: 2022-02-11
 * <p>
 * created-by: nmhillusion
 */
public enum LogLevel {
    TRACE(0, "TRACE", Level.TRACE, AnsiColor.ANSI_WHITE),
    DEBUG(1, "DEBUG", Level.DEBUG, AnsiColor.ANSI_PURPLE),
    INFO(2, "INFO", Level.INFO, AnsiColor.ANSI_GREEN),
    WARN(3, "WARN", Level.WARN, AnsiColor.ANSI_YELLOW),
    ERROR(4, "ERROR", Level.ERROR, AnsiColor.ANSI_RED),
    ;

    private final int priority;
    private final String value;
    private final String color;

    private final Level slf4jLevel;

    LogLevel(int priority, String value, Level slf4jLevel, String color) {
        this.priority = priority;
        this.value = value;
        this.slf4jLevel = slf4jLevel;
        this.color = color;
    }

    public int getPriority() {
        return priority;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public Level getSlf4jLevel() {
        return slf4jLevel;
    }
}
