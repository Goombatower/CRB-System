package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The store owns all {@link User}s, {@link Room}s and {@link Booking}s. It is
 * the only place where IDs are created and where some constraints are
 * enforced (unique IDs, no overlapping bookings, cascading deletes).
 *
 * <P>Methods order: Add, Get, Remove, Change.</P>
 * <p>Notice: User's priority check and selecting change methods should be done in controller.</p>
 */
public final class Storage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<Integer, Booking> bookings = new HashMap<>();

    // ------------------------------------------------------------------
    // Users
    // ------------------------------------------------------------------

    public User addUser(String name) {
        int userId = IdGenerator.nextUserId();
        User user = new User(userId, name, UserType.REGULAR);
        users.put(userId, user);
        return user;
    }

    public User getUser(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NoSuchUserException("no such user: " + userId);
        }
        return user;
    }

    public List<User> getUsers() {
        List<User> list = new ArrayList<>(users.values());
        // sort by id, ascending order
        list.sort(Comparator.comparingInt(User::getUserId));
        return list;
    }

    public void removeUser(int userId) {
        User user = users.remove(userId);
        if (user == null) {
            throw new CrobsException.NoSuchUserException("no such user: " + userId);
        }
        // remove bookings made by user
        for (Booking booking : bookings.values()) {
            if (booking.getUserId() == userId) {
                bookings.remove(booking.getBookId());
            }
        }
    }

    public void changeUserType(int userId, UserType newRole) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NoSuchUserException("no such user: " + userId);
        }
        user.setUserType(newRole);
        users.replace(userId, user);
    }

    // ------------------------------------------------------------------
    // Rooms
    // ------------------------------------------------------------------

    public Room addRoom(RoomType type, int capacity, Set<String> features) {
        int roomId = IdGenerator.nextRoomId();
        Room room = new Room(roomId, type, capacity, features);
        rooms.put(roomId, room);
        return room;
    }

    public Room getRoom(int roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such room: " + roomId);
        }
        return room;
    }

    public List<Room> getRooms() {
        List<Room> list = new ArrayList<>(rooms.values());
        // sort by id, ascending order
        list.sort(Comparator.comparingInt(Room::getRoomId));
        return list;
    }

    public void removeRoom(int roomId) {
        Room room = rooms.remove(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such room: " + roomId);
        }
        // remove bookings associated with this room
        for (Booking booking : bookings.values()) {
            if (booking.getRoomId() == roomId) {
                bookings.remove(booking.getBookId());
            }
        }
    }

    public void changeRoomFeature(int roomId, Set<String> features) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        room.setFeatures(features);
        rooms.replace(roomId, room);
    }

    public void changeRoomType(int roomId, RoomType type) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        room.setType(type);
        rooms.replace(roomId, room);
    }

    public void changeRoomCapacity(int roomId, int capacity) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        room.setCapacity(capacity);
        rooms.replace(roomId, room);
    }

    // Special searching methods related with Room

    // constraint checking helper
    private boolean isAvailable(int roomId, LocalDate date, LocalTime start, LocalTime end) {
        for (Booking booking : bookings.values()) {
            if (booking.getRoomId() == roomId && booking.getDate().equals(date)
                    && start.isBefore(booking.getEndTime())
                    && booking.getStartTime().isBefore(end)) {
                return false;
            }
        }
        return true;
    }

    /** Search rooms available for a slot with the required capacity and features. */
    public List<Room> searchAvailableRooms(LocalDate date, LocalTime start, LocalTime end,
                                           int minCapacity, Set<String> features) {
        List<Room> list = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (isAvailable(room.getRoomId(), date, start, end) && room.getFeatures().containsAll(features)
                    && room.getCapacity() >= minCapacity) {
                list.add(room);
            }
        }
        return list;
    }

    /** Returns all bookings for a room in the 7-day window starting at {@code weekStart} */
    public List<Booking> weeklySchedule(int roomId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(7); // Exclusive end for a 7-day window
        List<Booking> list = new ArrayList<>();

        for (Booking booking : bookings.values()) {
            if (booking.getRoomId() == roomId) {
                LocalDate date = booking.getDate();
                // Check if date is within [weekStart, weekStart + 6 days]
                if (date != null && !date.isBefore(weekStart) && date.isBefore(weekEnd)) {
                    list.add(booking);
                }
            }
        }
        // sort by date and time
        list.sort(Comparator.comparing(Booking::getDate).thenComparing(Booking::getStartTime));
        return list;
    }

    // ------------------------------------------------------------------
    // Bookings
    // ------------------------------------------------------------------

    public Booking addBooking(int userId, int roomId, LocalDate date,
                              LocalTime start, LocalTime end, String purpose) {
        if (!isAvailable(roomId, date, start, end)) {
            throw new CrobsException.BookingConflictException("booking conflict");
        }
        int bookId = IdGenerator.nextBookId();
        Booking booking = new Booking(bookId, userId, roomId, date, start, end, purpose);
        bookings.put(bookId, booking);
        return booking;
    }

    public Booking getBooking(int bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        return booking;
    }

    public List<Booking> getBookingsForUser(int userId) {
        List<Booking> list = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getUserId() == userId) {
                list.add(booking);
            }
        }
        // sort by date and time
        list.sort(Comparator.comparing(Booking::getDate).thenComparing(Booking::getStartTime));
        return list;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>(bookings.values());
        // sort by date and time
        list.sort(Comparator.comparing(Booking::getDate).thenComparing(Booking::getStartTime));
        return list;
    }

    public void removeBooking(int bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
    }

    public void changeBookingRoom(int bookingId, int roomId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setRoomId(roomId);
        bookings.replace(bookingId, booking);
    }

    public void changeBookingTime(int bookingId, LocalDate date, LocalTime start, LocalTime end) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setTime(date, start, end);
        bookings.replace(bookingId, booking);
    }

    public void changeBookingPurpose(int bookingId, String purpose) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setPurpose(purpose);
        bookings.replace(bookingId, booking);
    }
}