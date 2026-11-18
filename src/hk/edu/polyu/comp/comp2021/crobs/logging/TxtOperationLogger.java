package hk.edu.polyu.comp.comp2021.crobs.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/// logs the operations in txt
public final class TxtOperationLogger implements OperationLogger {
    private final BufferedWriter writer;

    /// @param path the path that needs to be logged
    /// @throws IOException throws exception to appease JAVA
    public TxtOperationLogger(Path path) throws IOException {
        this.writer =  new BufferedWriter(new FileWriter(path.toString()));
    }

    @Override
    public void log(int operationIndex, String command) {
        try {
            writer.write(command);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("failed to write TXT log", e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
