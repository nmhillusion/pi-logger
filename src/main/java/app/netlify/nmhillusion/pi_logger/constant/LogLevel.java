package app.netlify.nmhillusion.pi_logger.constant;


/**
 * date: 2022-02-11
 * <p>
 * created-by: nmhillusion
 */
public enum LogLevel {
    DEBUG("DEBUG", AnsiColor.ANSI_PURPLE),
    INFO("INFO", AnsiColor.ANSI_GREEN),
    WARN("WARN", AnsiColor.ANSI_YELLOW),
    ERROR("ERROR", AnsiColor.ANSI_RED),
    ;

    private final String value;
    private final String color;

    LogLevel(String value, String color) {
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
