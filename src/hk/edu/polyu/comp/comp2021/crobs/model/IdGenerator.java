package hk.edu.polyu.comp.comp2021.crobs.model;

/**
 * Generates unique IDs for rooms, users and bookings. Each kind of ID is
 * counted separately, starting from 1.
 */
public final class IdGenerator {
    private IdGenerator(){}

    /// The next room ID to hand out.
    static private int roomIdStart = 1;
    /// The next user ID to hand out.
    static private int userIdStart = 1;
    /// The next booking ID to hand out.
    static private int bookIdStart = 1;

    /// Generates the next room ID.
    /// @return a new unique room ID
    public static int nextRoomId() {
        return roomIdStart++;
    }

    /// Generates the next user ID.
    /// @return a new unique user ID
    public static int nextUserId() {
        return userIdStart++;
    }

    /// Generates the next booking ID.
    /// @return a new unique booking ID
    public static int nextBookId() {
        return bookIdStart++;
    }
}
