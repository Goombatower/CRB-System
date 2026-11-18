package hk.edu.polyu.comp.comp2021.crobs.controller;

import java.util.ArrayList;
import java.util.List;

/** Turns a raw command line into a {@link Command}. */
public final class CommandParser {

    public Command parse(String line) {
        if (line == null) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        // split with spaces
        String[] tokens = trimmed.split("\\s+");
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            args.add(tokens[i]);
        }
        return new Command(tokens[0], args);
    }
}