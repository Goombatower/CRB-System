package hk.edu.polyu.comp.comp2021.crobs.model;

public class IdGenerator {
    private IdGenerator(){}
    static int roomIdStart = 1;
    static int userIdStart = 1;
    static int bookIdStart = 1;

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
