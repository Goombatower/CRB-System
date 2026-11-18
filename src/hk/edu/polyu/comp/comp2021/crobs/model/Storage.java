package hk.edu.polyu.comp.comp2021.crobs.model;
import java.util.HashMap;
import java.util.Objects;

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
    // there could be multiple storages
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Room> rooms = new HashMap<>();
    private final Map<Integer, Booking> bookings = new HashMap<>();

    // ------------------------------------------------------------------
    // Users
    // ------------------------------------------------------------------

    /// Creates a new regular user with a newly generated ID.
    /// @param name the name of the new user
    /// @return the newly created user
    public User addUser(String name) {
        int userId = IdGenerator.nextUserId();
        User user = new User(userId, name, UserType.REGULAR);
        users.put(userId, user);
        return user;
    }

    /// Looks up a user by ID.
    /// @param userId the ID of the user to find
    /// @return the user with the given ID
    public User getUser(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NoSuchUserException("no such user: " + userId);
        }
        return user;
    }

    /// Gets all users.
    /// @return a list of all users, sorted by user ID in ascending order
    public List<User> getUsers() {
        List<User> list = new ArrayList<>(users.values());
        // sort by id, ascending order
        list.sort(Comparator.comparingInt(User::getUserId));
        return list;
    }

    /// Removes a regular user together with all bookings made by that user.
    /// @param userId the ID of the user to remove
    public void removeUser(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new CrobsException.NoSuchUserException("no such user: " + userId);
        }
        // only allow removing regular
        if (user.isAdmin()) {
            throw new CrobsException.NoSuchUserException("can only remove regular user");
        }
        users.remove(userId);
        // remove bookings made by user
        bookings.values().removeIf(booking -> booking.getUserId() == userId);
    }

    /// Changes the type of a user.
    ///
    /// @param userId the ID of the user to change
    /// @param newRole the new type to assign to the user
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

    /// Creates a new room with a newly generated ID.
    ///
    /// @param type the type of the new room
    /// @param capacity the capacity of the new room
    /// @param features the features of the new room
    /// @return the newly created room
    public Room addRoom(RoomType type, int capacity, Set<String> features) {
        int roomId = IdGenerator.nextRoomId();
        Room room = new Room(roomId, type, capacity, features);
        rooms.put(roomId, room);
        return room;
    }

    /// Looks up a room by ID.
    ///
    /// @param roomId the ID of the room to find
    /// @return the room with the given ID
    public Room getRoom(int roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such room: " + roomId);
        }
        return room;
    }

    /// Gets all rooms.
    ///
    /// @return a list of all rooms, sorted by room ID in ascending order
    public List<Room> getRooms() {
        List<Room> list = new ArrayList<>(rooms.values());
        // sort by id, ascending order
        list.sort(Comparator.comparingInt(Room::getRoomId));
        return list;
    }

    /// Removes a room together with all bookings of that room.
    ///
    /// @param roomId the ID of the room to remove
    public void removeRoom(int roomId) {
        Room room = rooms.remove(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such room: " + roomId);
        }
        // remove bookings associated with this room
        bookings.values().removeIf(booking -> booking.getRoomId() == roomId);
    }

    /// Replaces the features of a room.
    ///
    /// @param roomId the ID of the room to change
    /// @param features the new set of features
    public void changeRoomFeature(int roomId, Set<String> features) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        room.setFeatures(features);
        rooms.replace(roomId, room);
    }

    /// Changes the type of a room.
    ///
    /// @param roomId the ID of the room to change
    /// @param type the new room type
    public void changeRoomType(int roomId, RoomType type) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        room.setType(type);
        rooms.replace(roomId, room);
    }

    /// Changes the capacity of a room.
    ///
    /// @param roomId the ID of the room to change
    /// @param capacity the new capacity
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

    /**
     * Search rooms available for a slot with the required capacity and features.
     *
     * @param date the date of the slot
     * @param start the start time of the slot (inclusive)
     * @param end the end time of the slot (exclusive)
     * @param minCapacity the minimum capacity a room must have
     * @param features the features a room must all have
     * @return a list of the rooms that are free during the slot and meet the requirements
     */
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

    /**
     * Returns all bookings for a room in the 7-day window starting at {@code weekStart}.
     *
     * @param roomId the ID of the room
     * @param weekStart the first day of the 7-day window
     * @return the bookings of the room within the window, sorted by date and then start time
     */
    public List<Booking> weeklySchedule(int roomId, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(7); // Exclusive end for a 7-day window
        List<Booking> list = new ArrayList<>();

        for (Booking booking : bookings.values()) {
            if (booking.getRoomId() == roomId) {
                LocalDate date = booking.getDate();
                // Check if date is within [weekStart, weekStart + 6 days]
                if (!date.isBefore(weekStart) && date.isBefore(weekEnd)) {
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

    /// Creates a new booking with a newly generated ID, if the room exists and is free.
    ///
    /// @param userId the ID of the user making the booking
    /// @param roomId the ID of the room to book
    /// @param date the date of the booking
    /// @param start the start time of the booking (inclusive)
    /// @param end the end time of the booking (exclusive)
    /// @param purpose the purpose of the booking
    /// @return the newly created booking
    public Booking addBooking(int userId, int roomId, LocalDate date,
                              LocalTime start, LocalTime end, String purpose) {
        if (rooms.get(roomId) == null) {
            throw new CrobsException.NoSuchRoomException("no such user: " + roomId);
        }
        if (!isAvailable(roomId, date, start, end)) {
            throw new CrobsException.BookingConflictException("booking conflict");
        }
        int bookId = IdGenerator.nextBookId();
        Booking booking = new Booking(bookId, userId, roomId, date, start, end, purpose);
        bookings.put(bookId, booking);
        return booking;
    }

    /// Looks up a booking by ID.
    ///
    /// @param bookingId the ID of the booking to find
    /// @return the booking with the given ID
    public Booking getBooking(int bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        return booking;
    }

    /// Gets all bookings made by a user.
    ///
    /// @param userId the ID of the user
    /// @return the user's bookings, sorted by date and then start time
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

    /// Gets all bookings.
    ///
    /// @return a list of all bookings, sorted by date and then start time
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>(bookings.values());
        // sort by date and time
        list.sort(Comparator.comparing(Booking::getDate).thenComparing(Booking::getStartTime));
        return list;
    }

    /// Removes a booking.
    ///
    /// @param bookingId the ID of the booking to remove
    public void removeBooking(int bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
    }

    /// Moves a booking to another room.
    ///
    /// @param bookingId the ID of the booking to change
    /// @param roomId the ID of the new room
    public void changeBookingRoom(int bookingId, int roomId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setRoomId(roomId);
        bookings.replace(bookingId, booking);
    }

    /// Changes the date and time interval of a booking.
    ///
    /// @param bookingId the ID of the booking to change
    /// @param date the new date
    /// @param start the new start time (inclusive)
    /// @param end the new end time (exclusive)
    public void changeBookingTime(int bookingId, LocalDate date, LocalTime start, LocalTime end) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setTime(date, start, end);
        bookings.replace(bookingId, booking);
    }

    /// Changes the purpose of a booking.
    /// @param bookingId the ID of the booking to change
    /// @param purpose the new purpose
    public void changeBookingPurpose(int bookingId, String purpose) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new CrobsException.NoSuchBookingException("no such booking: " + bookingId);
        }
        booking.setPurpose(purpose);
        bookings.replace(bookingId, booking);
    }
}