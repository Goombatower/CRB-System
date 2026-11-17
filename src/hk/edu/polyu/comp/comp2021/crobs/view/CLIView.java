package hk.edu.polyu.comp.comp2021.crobs.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * The command-line view of Crobs: reads commands from an input stream and writes
 * results to an output stream.
 */
public class CLIView implements AutoCloseable{
    private final BufferedReader in;
    private final PrintWriter out;

    public CLIView(InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = new PrintWriter(out, true);
    }

    public String readCommand() throws IOException {
        out.print("> ");
        out.flush();
        return in.readLine();
    }

    public void println(String line) {
        out.println(line);
    }

    @Override
    public void close() {
        out.flush();
    }
}