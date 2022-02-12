package app.netlify.nmhillusion.pi_logger.output;

import java.io.IOException;
import java.io.PrintStream;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class ConsoleOutputWriter extends IOutputWriter {
    private final PrintStream printStream = new PrintStream(System.out, true);

    @Override
    protected PrintStream getOutputPrintStream() {
        return printStream;
    }

    @Override
    public void doOutput(String outputMessage, Throwable throwable) throws IOException {
        super.doOutput(outputMessage, throwable);

        if (null != throwable) {
            throwable.printStackTrace();
        }
    }
}
