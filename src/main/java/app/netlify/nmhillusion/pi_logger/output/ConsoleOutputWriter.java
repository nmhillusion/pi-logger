package app.netlify.nmhillusion.pi_logger.output;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

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
    public void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        super.doOutput(outputMessage, throwableList);

        if (null != throwableList) {
            for (final Throwable throwable_ : throwableList) {
                throwable_.printStackTrace();
            }
        }
    }
}
