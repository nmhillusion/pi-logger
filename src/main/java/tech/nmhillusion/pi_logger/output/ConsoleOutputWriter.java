package tech.nmhillusion.pi_logger.output;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */

public class ConsoleOutputWriter implements IOutputWriter {

    @Override
    public void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        final PrintStream printStream = System.out;
        printStream.println(outputMessage);

        if (null != throwableList) {
            for (final Throwable throwable_ : throwableList) {
                throwable_.printStackTrace(printStream);
            }
        }
    }
}
