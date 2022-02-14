package app.netlify.nmhillusion.pi_logger.constant;


/**
 * date: 2022-02-11
 * <p>
 * created-by: nmhillusion
 */
public enum LogLevel {
    TRACE(0, "TRACE", AnsiColor.ANSI_WHITE),
    DEBUG(1, "DEBUG", AnsiColor.ANSI_PURPLE),
    INFO(2, "INFO", AnsiColor.ANSI_GREEN),
    WARN(3, "WARN", AnsiColor.ANSI_YELLOW),
    ERROR(4, "ERROR", AnsiColor.ANSI_RED),
    ;

    private final int priority;
    private final String value;
    private final String color;

    LogLevel(int priority, String value, String color) {
        this.priority = priority;
        this.value = value;
        this.color = color;
    }

    public String getPriority() {
        return priority;
    }
    
    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
