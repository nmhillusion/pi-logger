package tech.nmhillusion.pi_logger.output;

import java.io.IOException;
import java.util.List;

/**
 * date: 2022-02-12
 * <p>
 * created-by: nmhillusion
 */
public interface IOutputWriter {
    void doOutput(String outputMessage, List<Throwable> throwableList) throws IOException;

    default void flush() throws IOException {}

    default void close() throws IOException {}
}
