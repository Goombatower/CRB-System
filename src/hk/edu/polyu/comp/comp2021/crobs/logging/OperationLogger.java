package hk.edu.polyu.comp.comp2021.crobs.logging;

import java.io.Closeable;
import java.io.IOException;
/**
 *  Records the operations performed during a Crobs session
 */
public interface OperationLogger extends Closeable {

    /**
     * Records one operation.
     *
     * @param operationIndex the 1-based index of the operation, shared by all loggers
     * @param command        the command text as typed
     */
    void log(int operationIndex, String command);

    /**
     * Flushes and closes the underlying writer.
     * @throws IOException thrown by writer
     */
    @Override
    void close() throws IOException;
}

