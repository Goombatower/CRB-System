package hk.edu.polyu.comp.comp2021.crobs.model;
import java.util.Random;

public class IdGenerator {
    private IdGenerator(){};
    private static final Random randomID = new Random();

    // temporary function, should be modified, uniqueness not ensured
    public static int nextRoomID() {
        return randomID.nextInt(100, 1000);
    }
    public static int nextUserID() {
        return randomID.nextInt(100000, 1000000);
    }
}
