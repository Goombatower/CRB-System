package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.logging.OperationLogger;
import hk.edu.polyu.comp.comp2021.crobs.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
Command references:
    addUser name
    removeUser targetUserID
    changeRole targetUserId newRole
    login userID
    logout
    addRoom roomType capacity features
    modifyRoom roomID field newValue
    deleteRoom roomID
    book roomId date startTime endTime purpose
    modifyBooking bookingID roomId date startTime endTime purpose
    deleteBooking bookingID
    search date startTime endTime minCapacity features
    schedule roomId weekStartDate
    listRoom roomID
    listAllRooms
    listBookings
    quit
*/

/**
 * Interprets commands, checks permissions, matches operations in the {@link Storage} and
 * formats results for the view.
 *
 * <p>It owns the current {@link Session} and encodes the
 * rules for which role may perform which operation. Each public command method
 * maps to one requirement (REQ2...REQ18).</p>
 */
public final class Controller {
    // initialize formatter
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // fields
    private final Storage store;
    private final Session session = new Session();
    private final List<OperationLogger> loggers;
    private int operationIndex = 0; // record for loggers
    private boolean quitRequested = false;

    /// @param store the storage of users, bookings, and others
    /// @param loggers the log of any given session
    public Controller(Storage store, List<OperationLogger> loggers) {
        this.store = store;
        this.loggers = new ArrayList<>(loggers);
    }

    // getters
    /// @return the storage
    public Storage getStore() {
        return store;
    }

    /// @return the session
    public Session getSession() {
        return session;
    }

    /// @return the request to quit a session
    public boolean isQuitRequested() {
        return quitRequested;
    }

    /**
     * Main function of controller.
     * Executes one command: logs it, dispatches it, and returns a message
     * describing the outcome. Errors are turned into messages rather than thrown
     * so the main loop keeps running.
     * @param command the command to be executed
     * @return a dispatch if the command is succesful, and an error if it's not
     */
    public String execute(Command command) {
        log(command);
        try {
            return dispatch(command);
        } catch (CrobsException e) {
            return "Error: " + e.getMessage();
        }
    }

    private void log(Command command) {
        operationIndex++;
        for (OperationLogger logger : loggers) {
            logger.log(operationIndex, command.toString());
        }
    }

    private String dispatch(Command command) {
        return switch (command.getName()) {
            case "addUser" -> addUser(command);
            case "removeUser" -> removeUser(command);
            case "changeRole" -> changeRole(command);
            case "login" -> login(command);
            case "logout" -> logout(command);
            case "addRoom" -> addRoom(command);
            case "modifyRoom" -> modifyRoom(command);
            case "deleteRoom" -> deleteRoom(command);
            case "book" -> book(command);
            case "modifyBooking" -> modifyBooking(command);
            case "deleteBooking" -> deleteBooking(command);
            case "search" -> search(command);
            case "schedule" -> schedule(command);
            case "listRoom" -> listRoom(command);
            case "listAllRooms" -> listAllRooms(command);
            case "listBookings" -> listBookings(command);
            case "quit" -> quit(command);
            default -> throw new CrobsException.InvalidCommandException("unknown command: " + command.getName());
        };
    }

    // permission and parsing helpers

    private User requireLogin() {
        if (!session.isLoggedIn()) {
            throw new CrobsException.PermissionDeniedException("you must log in first");
        }
        return session.getCurrentUser();
    }

    private User requireAdmin() {
        User user = requireLogin();
        if (!user.isAdmin()) {
            throw new CrobsException.PermissionDeniedException("admin privileges required");
        }
        return user;
    }

    private User requireSuperAdmin() {
        User user = requireLogin();
        if (!user.isSuperAdmin()) {
            throw new CrobsException.PermissionDeniedException("superadmin privileges required");
        }
        return user;
    }

    private String requireArg(Command command, int index, String usage) {
        String value = command.arg(index);
        if (value == null) {
            throw new CrobsException.InvalidCommandException("usage: " + usage);
        }
        return value;
    }

    private int parseInt(String token) {
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new CrobsException.InvalidValueException("invalid number: " + token);
        }
    }

    private LocalDate parseDate(String token) {
        try {
            return LocalDate.parse(token, DATE_FORMAT);
        } catch (RuntimeException e) {
            throw new CrobsException.InvalidValueException(
                    "invalid date: " + token + " (expected yyyy-MM-dd)");
        }
    }

    private LocalTime parseTime(String token) {
        try {
            return LocalTime.parse(token, TIME_FORMAT);
        } catch (RuntimeException e) {
            throw new CrobsException.InvalidValueException(
                    "invalid time: " + token + " (expected HH:mm)");
        }
    }

    private Set<String> parseFeatures(String token) {
        // split token into String array and convert the array into a list to form the set
        return new LinkedHashSet<>(Arrays.asList(token.split(",")));
    }

    // commands, order matches requirements

    /** REQ2: addUser name — admin only. */
    private String addUser(Command c) {
        User admin = requireAdmin();
        String name = requireArg(c, 0, "addUser name");
        User user = store.addUser(name);
        return "Added user " + user;
    }

    /** REQ3: removeUser targetUserID — admin only. */
    private String removeUser(Command c) {
        User admin = requireAdmin();
        int id = parseInt(requireArg(c, 0, "removeUser targetUserID"));
        store.removeUser(id);
        return "Removed user " + id;
    }

    /** REQ4: changeRole targetUserId newRole — superadmin only. */
    private String changeRole(Command c) {
        User superAdmin = requireSuperAdmin();
        int id = parseInt(requireArg(c, 0, "changeRole targetUserId newRole"));
        UserType userType = UserType.fromToken(requireArg(c, 1, "changeRole targetUserId newRole"));
        // The superadmin keeps its own role and may only modify other users.
        if (id == superAdmin.getUserId()) {
            throw new CrobsException.PermissionDeniedException(
                    "superadmin cannot change its own role");
        }
        store.changeUserType(id, userType);
        return "Changed role of user " + id + " to " + userType;
    }

    /** REQ5: login userID. */
    private String login(Command c) {
        if (session.isLoggedIn()) {
            throw new CrobsException.PermissionDeniedException(
                    "already logged in; log out before logging in again");
        }
        int id = parseInt(requireArg(c, 0, "login userID"));
        User user = store.getUser(id);
        session.setCurrentUser(user);
        return "Logged in as " + user.getName() + " (id " + user.getUserId() + ")";
    }

    /** REQ6: logout. */
    private String logout(Command c) {
        if (!session.isLoggedIn()) {
            throw new CrobsException.PermissionDeniedException("no user is logged in");
        }
        User user = session.getCurrentUser();
        session.logout();
        return "Logged out " + user.getName() + " (id " + user.getUserId() + ")";
    }

    /** REQ7: addRoom roomType capacity features — admin only. */
    private String addRoom(Command c) {
        User admin = requireAdmin();
        RoomType type = RoomType.fromToken(requireArg(c, 0, "addRoom roomType capacity features"));
        int capacity = parseInt(requireArg(c, 1, "addRoom roomType capacity features"));
        Set<String> features = parseFeatures(requireArg(c, 2, "addRoom roomType capacity features"));
        Room room = store.addRoom(type, capacity, features);
        return "Added room " + room;
    }

    /** REQ8: modifyRoom roomID field newValue — admin only. */
    private String modifyRoom(Command c) {
        User admin = requireAdmin();
        int id = parseInt(requireArg(c, 0, "modifyRoom roomID field newValue"));
        String field = requireArg(c, 1, "modifyRoom roomID field newValue");
        String value = requireArg(c, 2, "modifyRoom roomID field newValue");
        switch (field){
            case "type" -> store.changeRoomType(id, RoomType.fromToken(value));
            case "capacity" -> store.changeRoomCapacity(id, parseInt(value));
            case "features" -> store.changeRoomFeature(id, parseFeatures(value));
            default -> throw new CrobsException.InvalidValueException("invalid field: " + field);
        }
        return "Modified room " + id;
    }

    /** REQ9: deleteRoom roomID — admin only. */
    private String deleteRoom(Command c) {
        User admin = requireAdmin();
        int id = parseInt(requireArg(c, 0, "deleteRoom roomID"));
        store.removeRoom(id);
        return "Deleted room " + id;
    }

    /** REQ10: book roomId date startTime endTime purpose — any logged-in user. */
    private String book(Command c) {
        User user = requireLogin();
        int roomId = parseInt(requireArg(c, 0, "book roomId date startTime endTime purpose"));
        LocalDate date = parseDate(requireArg(c, 1, "book roomId date startTime endTime purpose"));
        LocalTime start = parseTime(requireArg(c, 2, "book roomId date startTime endTime purpose"));
        LocalTime end = parseTime(requireArg(c, 3, "book roomId date startTime endTime purpose"));
        String purpose = requireArg(c, 4, "book roomId date startTime endTime purpose");
        Booking booking = store.addBooking(user.getUserId(), roomId, date, start, end, purpose);
        return "Created booking " + booking;
    }

    /** REQ11: modifyBooking bookingID roomId date startTime endTime purpose — any logged-in user. */
    private String modifyBooking(Command c) {
        User user = requireLogin();
        int bookingId = parseInt(requireArg(c, 0,
                "modifyBooking bookingID roomId date startTime endTime purpose"));
        int roomId = parseInt(requireArg(c, 1,
                "modifyBooking bookingID roomId date startTime endTime purpose"));
        LocalDate date = parseDate(requireArg(c, 2,
                "modifyBooking bookingID roomId date startTime endTime purpose"));
        LocalTime start = parseTime(requireArg(c, 3,
                "modifyBooking bookingID roomId date startTime endTime purpose"));
        LocalTime end = parseTime(requireArg(c, 4,
                "modifyBooking bookingID roomId date startTime endTime purpose"));
        String purpose = requireArg(c, 5,
                "modifyBooking bookingID roomId date startTime endTime purpose");
        store.changeBookingRoom(bookingId, roomId);
        store.changeBookingTime(bookingId, date, start, end);
        store.changeBookingPurpose(bookingId, purpose);
        return "Modified booking " + bookingId;
    }

    /** REQ12: deleteBooking bookingID — any logged-in user. */
    private String deleteBooking(Command c) {
        User user = requireLogin();
        int id = parseInt(requireArg(c, 0, "deleteBooking bookingID"));
        store.removeBooking(id);
        return "Deleted booking " + id;
    }

    /** REQ13: search date startTime endTime minCapacity features — any logged-in user. */
    private String search(Command c) {
        User user = requireLogin();
        LocalDate date = parseDate(requireArg(c, 0, "search date startTime endTime minCapacity features"));
        LocalTime start = parseTime(requireArg(c, 1, "search date startTime endTime minCapacity features"));
        LocalTime end = parseTime(requireArg(c, 2, "search date startTime endTime minCapacity features"));
        int minCapacity = parseInt(requireArg(c, 3, "search date startTime endTime minCapacity features"));
        Set<String> features = parseFeatures(requireArg(c, 4, "search date startTime endTime minCapacity features"));
        return formatRooms(store.searchAvailableRooms(date, start, end, minCapacity, features));
    }

    /** REQ14: schedule roomId weekStartDate — any logged-in user. */
    private String schedule(Command c) {
        User user = requireLogin();
        int roomId = parseInt(requireArg(c, 0, "schedule roomId weekStartDate"));
        LocalDate weekStart = parseDate(requireArg(c, 1, "schedule roomId weekStartDate"));
        store.getRoom(roomId); // ensure the room exists
        List<Booking> bookings = store.weeklySchedule(roomId, weekStart);
        // a regular user may only see the user ID of their own booking
        // an admin sees every booking's user ID.
        // done in formatting
        return formatSchedule(bookings, user);
    }

    /** REQ15: listRoom roomID — any logged-in user. */
    private String listRoom(Command c) {
        User user = requireLogin();
        int id = parseInt(requireArg(c, 0, "listRoom roomID"));
        return formatRoom(store.getRoom(id));
    }

    /** REQ16: listAllRooms — any logged-in user. */
    private String listAllRooms(Command c) {
        User user = requireLogin();
        return formatRooms(store.getRooms());
    }

    /** REQ17: listBookings — any logged-in user. */
    private String listBookings(Command c) {
        User user = requireLogin();
        return formatSchedule(store.getBookingsForUser(user.getUserId()), user);
    }

    /** REQ18: quit. */
    private String quit(Command c) {
        quitRequested = true;
        return "Bye";
    }

    // output formatting

    private String formatRoom(Room room) {
        return room.toString();
    }

    private String formatRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return "(no rooms)";
        }
        StringBuilder sb = new StringBuilder();
        for (Room room : rooms) {
            if (!sb.isEmpty()) {
                sb.append('\n');
            }
            sb.append(formatRoom(room));
        }
        return sb.toString();
    }

    private String formatSchedule(List<Booking> bookings, User user) {
        if (bookings.isEmpty()) {
            return "(no bookings)";
        }
        StringBuilder sb = new StringBuilder();
        for (Booking b : bookings) {
            if (!sb.isEmpty()) {
                sb.append('\n');
            }
            sb.append("Booking ").append(b.getBookId())
                    .append(" room ").append(b.getRoomId())
                    .append(' ').append(b.getDate())
                    .append(' ').append(b.getStartTime()).append('-').append(b.getEndTime())
                    .append(' ').append(b.getPurpose());
            // id visibility
            if (user.isAdmin()) {
                sb.append(" (user ").append(b.getUserId()).append(')');
            }
            else if (b.getUserId() == user.getUserId()) {
                sb.append(" (user [you]").append(b.getUserId()).append(')');
            }
        }
        return sb.toString();
    }
}