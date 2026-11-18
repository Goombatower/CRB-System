package hk.edu.polyu.comp.comp2021.crobs.controller;

import java.util.Collections;
import java.util.List;

/** The class describing parsed command objects. */
public final class Command {

    private final String name;
    private final List<String> args;

    /**
     * @param name name of the user
     * @param args args
     */
    public Command(String name, List<String> args) {
        this.name = name;
        this.args = Collections.unmodifiableList(args);
    }

    /**
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * @return the args
     */
    public List<String> getArgs() {
        return args;
    }

    /** @param index index of the command
     * @return the argument at {@code index}, or {@code null} if absent. */
    public String arg(int index) {
        return index < args.size() ? args.get(index) : null;
    }

    /**
     * @return the number of args
     */
    public int argCount() {
        return args.size();
    }

    /** @return the original command text reconstructed from name and args. */
    @Override
    public String toString() {
        return args.isEmpty() ? name : name + " " + String.join(" ", args);
    }
}
