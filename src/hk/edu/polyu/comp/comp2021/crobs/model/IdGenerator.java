package hk.edu.polyu.comp.comp2021.crobs.model;

public final class IdGenerator {
    private IdGenerator(){}
    static private int roomIdStart = 1;
    static private int userIdStart = 1;
    static private int bookIdStart = 1;

    public static int nextRoomId() {
        return roomIdStart++;
    }
    public static int nextUserId() {
        return userIdStart++;
    }
    public static int nextBookId() {
        return bookIdStart++;
    }
}
