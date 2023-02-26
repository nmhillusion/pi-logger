package app.netlify.nmhillusion.pi_logger.output;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */
public abstract class IOutputWriter {
    protected abstract PrintStream getOutputPrintStream();

    public void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException {
        final PrintStream outputStreamWriter = getOutputPrintStream();
        if (null != outputStreamWriter) {
            outputStreamWriter
                    .println(outputMessage);
        }
    }
}
