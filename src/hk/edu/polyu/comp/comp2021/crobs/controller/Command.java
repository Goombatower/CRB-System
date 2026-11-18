package hk.edu.polyu.comp.comp2021.crobs.controller;

import java.util.Collections;
import java.util.List;

/** The class describing parsed command objects. */
public final class Command {

    private final String name;
    private final List<String> args;

    public Command(String name, List<String> args) {
        this.name = name;
        this.args = Collections.unmodifiableList(args);
    }

    public String getName() {
        return name;
    }

    public List<String> getArgs() {
        return args;
    }

    /** @return the argument at {@code index}, or {@code null} if absent. */
    public String arg(int index) {
        return index < args.size() ? args.get(index) : null;
    }

    public int argCount() {
        return args.size();
    }

    /** @return the original command text reconstructed from name and args. */
    @Override
    public String toString() {
        return args.isEmpty() ? name : name + " " + String.join(" ", args);
    }
}
