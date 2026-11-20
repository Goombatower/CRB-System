package hk.edu.polyu.comp.comp2021.crobs;

import hk.edu.polyu.comp.comp2021.crobs.controller.*;
import hk.edu.polyu.comp.comp2021.crobs.model.*;
import hk.edu.polyu.comp.comp2021.crobs.logging.*;
import hk.edu.polyu.comp.comp2021.crobs.view.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point of Crobs.
 *
 * <p>Parses the {@code -html} and {@code -txt} log file arguments (REQ1), wires
 * the MVC components together and runs the command loop.</p>
 *
 * <p>Usage:
 * java hk.edu.polyu.comp.comp2021.crobs.Application -html log.html -txt log.txt</p>
 */
public final class Application {
    private Application() {
        // utility class, empty by default
    }

    /**
     * @param args the args
     * @throws IOException throws the IOexception of appeasement
     */
    public static void main(String[] args) throws IOException {
        Path htmlPath = null;
        Path txtPath = null;

        // parse args
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-html":
                    htmlPath = Paths.get(requireValue(args, i));
                    i++;
                    break;
                case "-txt":
                    txtPath = Paths.get(requireValue(args, i));
                    i++;
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    printUsage();
                    System.exit(1);
            }
        }

        // check path
        if (htmlPath == null || txtPath == null) {
            System.err.println("Missing argument -html and/or -txt.");
            printUsage();
            System.exit(1);
        }

        // check file
        if (Files.notExists(htmlPath)) {
            System.err.println("Cannot confirm html file, please create one or correct the path.");
            printUsage();
            System.exit(1);
        }
        if (Files.notExists(txtPath)) {
            System.err.println("Cannot confirm txt file, please create one or correct the path.");
            printUsage();
            System.exit(1);
        }

        run(htmlPath, txtPath);
    }

    // get argument value
    private static String requireValue(String[] args, int index) {
        if (index + 1 >= args.length) {
            System.err.println("Missing value after " + args[index]);
            printUsage();
            System.exit(1);
        }
        return args[index + 1];
    }

    // hint usage
    private static void printUsage() {
        System.out.println("Usage: java hk.edu.polyu.comp.comp2021.crobs.Application -html <log.html> -txt <log.txt>");
    }

    // main run function
    private static void run(Path htmlPath, Path txtPath) throws IOException {
        Storage store = new Storage();

        // initialize superadmin
        store.addUser("superadmin").setUserType(UserType.SUPERADMIN);

        // initialize loggers and logger holder
        List<OperationLogger> loggers = new ArrayList<>();
        loggers.add(new HtmlOperationLogger(htmlPath));
        loggers.add(new TxtOperationLogger(txtPath));

        // initialize controller and commandParser
        Controller controller = new Controller(store, loggers);
        CommandParser parser = new CommandParser();

        try (CLIView view = new CLIView(System.in, System.out)) {
            view.println("Crobs - Campus Room Booking System");
            view.println("The superadmin is user id 1, please log in with 'login 1'.");
            view.println("Type 'help' to check supported commands.");
            view.println("Type 'quit' to exit.");

            // main loop of MVC
            // in this loop:
            // view: receives one command
            // controller: parses the command
            // logging: log the command here
            // controller: calls the model to read or update data
            // model: updates its state and returns data to the controller
            // controller: passes message or required data to view
            // view: display the data
            while (!controller.isQuitRequested()) {
                String line = view.readCommand();
                // if user manually closed terminal without using quit
                if (line == null) {
                    break;
                }
                Command command = parser.parse(line);
                if (command == null) {
                    continue; // blank line
                }
                String result = controller.execute(command);
                if (result != null && !result.isEmpty()) {
                    view.println(result);
                }
            }
        } finally {
            // close loggers
            for (OperationLogger logger : loggers) {
                logger.close();
            }
        }
    }
}
