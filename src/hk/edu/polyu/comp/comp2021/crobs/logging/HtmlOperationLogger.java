package hk.edu.polyu.comp.comp2021.crobs.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * logs the operations in html
 */
public final class HtmlOperationLogger implements OperationLogger {
    private final BufferedWriter writer;

    /**
     * @param path the path that needs to be logged
     * @throws IOException throws exception to appease JAVA
     */
    public HtmlOperationLogger(Path path) throws IOException{
        this.writer =  new BufferedWriter(new FileWriter(path.toString()));
        // initialize HTML with a table
        write("<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <title>Crobs Log</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <h1>Crobs operations</h1>\n"
                + "    <table>\n"
                + "        <tr><th>#</th><th>Command</th></tr>\n");
    }

    @Override
    public void log(int operationIndex, String command) {
        // write operations into the table
        write("        <tr><td>" + operationIndex + "</td><td>" + escape(command) + "</td></tr>\n");
    }

    @Override
    public void close() throws IOException {
        // enclose HTML format and close
        write("</table>\n</body>\n</html>\n");
        writer.flush();
        writer.close();
    }

    private void write(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException("failed to write HTML log", e);
        }
    }

    // replace signs with special meanings in HTML
    private static String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
