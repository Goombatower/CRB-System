package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import hk.edu.polyu.comp.comp2021.crobs.model.RoomType;
import hk.edu.polyu.comp.comp2021.crobs.model.Storage;
import hk.edu.polyu.comp.comp2021.crobs.model.User;
import hk.edu.polyu.comp.comp2021.crobs.model.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageTest {

    private static final LocalDate MON = LocalDate.of(2026, 3, 2);
    private static final LocalTime T9 = LocalTime.of(9, 0);
    private static final LocalTime T10 = LocalTime.of(10, 0);
    private static final LocalTime T11 = LocalTime.of(11, 0);
    private static final LocalTime T12 = LocalTime.of(12, 0);

    private Storage storage;

    @BeforeEach
    void setUp() {
        storage = new Storage();
    }

    private static Set<String> features(String... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    // ------------------------------------------------------------------
    // Users
    // ------------------------------------------------------------------

    @Test
    void addUserCreatesRegularUser() {
        User user = storage.addUser("Alice");

        assertAll(
                () -> assertEquals("Alice", user.getName()),
                () -> assertEquals(UserType.REGULAR, user.getUserType()),
                () -> assertSame(user, storage.getUser(user.getUserId()))
        );
    }

    @Test
    void getUserThrowsWhenUserDoesNotExist() {
        assertThrows(CrobsException.NoSuchUserException.class, () -> storage.getUser(-1));
    }

    @Test
    void getUsersReturnsAllUsersSortedById() {
        User first = storage.addUser("A");
        User second = storage.addUser("B");
        User third = storage.addUser("C");

        List<User> users = storage.getUsers();

        assertEquals(Arrays.asList(first, second, third), users);
        for (int i = 1; i < users.size(); i++) {
            assertTrue(users.get(i - 1).getUserId() < users.get(i).getUserId());
        }
    }

    @Test
    void getUsersReturnsEmptyListWhenStorageIsEmpty() {
        assertTrue(storage.getUsers().isEmpty());
    }

    @Test
    void removeUserDeletesUserAndTheirBookings() {
        User alice = storage.addUser("Alice");
        User bob = storage.addUser("Bob");
        Room room = storage.addRoom(RoomType.MEETING_ROOM, 10, features());
        Booking aliceBooking = storage.addBooking(alice.getUserId(), room.getRoomId(),
                MON, T9, T10, "alice");
        Booking bobBooking = storage.addBooking(bob.getUserId(), room.getRoomId(),
                MON, T10, T11, "bob");

        storage.removeUser(alice.getUserId());

        assertAll(
                () -> assertThrows(CrobsException.NoSuchUserException.class,
                        () -> storage.getUser(alice.getUserId())),
                () -> assertThrows(CrobsException.NoSuchBookingException.class,
                        () -> storage.getBooking(aliceBooking.getBookId())),
                () -> assertEquals(bobBooking, storage.getBooking(bobBooking.getBookId()))
        );
    }

    @Test
    void removeUserThrowsWhenUserDoesNotExist() {
        assertThrows(CrobsException.NoSuchUserException.class, () -> storage.removeUser(-1));
    }

    @Test
    void removeUserRejectsAdmin() {
        User admin = storage.addUser("Admin");
        storage.changeUserType(admin.getUserId(), UserType.ADMIN);

        assertThrows(CrobsException.PermissionDeniedException.class,
                () -> storage.removeUser(admin.getUserId()));
    }

    @Test
    void changeUserTypeUpdatesRole() {
        User user = storage.addUser("Alice");

        storage.changeUserType(user.getUserId(), UserType.SUPERADMIN);

        assertEquals(UserType.SUPERADMIN, storage.getUser(user.getUserId()).getUserType());
    }

    @Test
    void changeUserTypeThrowsWhenUserDoesNotExist() {
        assertThrows(CrobsException.NoSuchUserException.class,
                () -> storage.changeUserType(-1, UserType.ADMIN));
    }

    // ------------------------------------------------------------------
    // Rooms
    // ------------------------------------------------------------------

    @Test
    void addRoomStoresRoom() {
        Room room = storage.addRoom(RoomType.COMPUTER_LAB, 30, features("os"));

        assertAll(
                () -> assertSame(room, storage.getRoom(room.getRoomId())),
                () -> assertEquals(RoomType.COMPUTER_LAB, room.getRoomType()),
                () -> assertEquals(30, room.getCapacity()),
                () -> assertEquals(features("os"), room.getFeatures())
        );
    }

    @Test
    void getRoomThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class, () -> storage.getRoom(-1));
    }

    @Test
    void getRoomsReturnsAllRoomsSortedById() {
        Room first = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room second = storage.addRoom(RoomType.MEETING_ROOM, 20, features());
        Room third = storage.addRoom(RoomType.COMPUTER_LAB, 30, features());

        List<Room> rooms = storage.getRooms();

        assertEquals(Arrays.asList(first, second, third), rooms);
        for (int i = 1; i < rooms.size(); i++) {
            assertTrue(rooms.get(i - 1).getRoomId() < rooms.get(i).getRoomId());
        }
    }

    @Test
    void getRoomsReturnsEmptyListWhenStorageIsEmpty() {
        assertTrue(storage.getRooms().isEmpty());
    }

    @Test
    void removeRoomDeletesRoomAndItsFutureBookings() {
        User user = storage.addUser("Alice");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Room removed = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room kept = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking removedBooking = storage.addBooking(user.getUserId(), removed.getRoomId(),
                tomorrow, T9, T10, "removed");
        Booking keptBooking = storage.addBooking(user.getUserId(), kept.getRoomId(),
                tomorrow, T9, T10, "kept");

        storage.removeRoom(removed.getRoomId());

        assertAll(
                () -> assertThrows(CrobsException.NoSuchRoomException.class,
                        () -> storage.getRoom(removed.getRoomId())),
                () -> assertThrows(CrobsException.NoSuchBookingException.class,
                        () -> storage.getBooking(removedBooking.getBookId())),
                () -> assertEquals(keptBooking, storage.getBooking(keptBooking.getBookId()))
        );
    }

    @Test
    void removeRoomKeepsPastBookings() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking past = storage.addBooking(user.getUserId(), room.getRoomId(),
                LocalDate.now().minusDays(1), T9, T10, "past");

        storage.removeRoom(room.getRoomId());

        assertEquals(past, storage.getBooking(past.getBookId()));
    }

    @Test
    void removeRoomThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class, () -> storage.removeRoom(-1));
    }

    @Test
    void changeRoomFeatureReplacesFeatures() {
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features("projector"));

        storage.changeRoomFeature(room.getRoomId(), features("tv", "whiteboard"));

        assertEquals(features("tv", "whiteboard"), storage.getRoom(room.getRoomId()).getFeatures());
    }

    @Test
    void changeRoomFeatureThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.changeRoomFeature(-1, features("tv")));
    }

    @Test
    void changeRoomTypeUpdatesType() {
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        storage.changeRoomType(room.getRoomId(), RoomType.LECTURE_THEATRE);

        assertEquals(RoomType.LECTURE_THEATRE, storage.getRoom(room.getRoomId()).getRoomType());
    }

    @Test
    void changeRoomTypeThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.changeRoomType(-1, RoomType.SEMINAR_ROOM));
    }

    @Test
    void changeRoomCapacityUpdatesCapacity() {
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        storage.changeRoomCapacity(room.getRoomId(), 42);

        assertEquals(42, storage.getRoom(room.getRoomId()).getCapacity());
    }

    @Test
    void changeRoomCapacityThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.changeRoomCapacity(-1, 10));
    }

    // ------------------------------------------------------------------
    // Searching rooms and schedules
    // ------------------------------------------------------------------

    @Test
    void searchAvailableRoomsReturnsEmptyListWhenStorageIsEmpty() {
        assertTrue(storage.searchAvailableRooms(MON, T9, T10, 1, features()).isEmpty());
    }

    @Test
    void searchAvailableRoomsFiltersByAvailabilityFeaturesAndCapacity() {
        User user = storage.addUser("Alice");
        Room available = storage.addRoom(RoomType.SEMINAR_ROOM, 20, features("projector"));
        Room tooSmall = storage.addRoom(RoomType.MEETING_ROOM, 4, features("projector"));
        Room lacksFeature = storage.addRoom(RoomType.MEETING_ROOM, 20, features("whiteboard"));
        Room booked = storage.addRoom(RoomType.LECTURE_THEATRE, 50, features("projector"));
        storage.addBooking(user.getUserId(), booked.getRoomId(), MON, T10, T11, "conflict");

        List<Room> result = storage.searchAvailableRooms(MON, LocalTime.of(9, 30),
                LocalTime.of(10, 30), 10, features("projector"));

        assertAll(
                () -> assertTrue(result.contains(available)),
                () -> assertFalse(result.contains(tooSmall)),
                () -> assertFalse(result.contains(lacksFeature)),
                () -> assertFalse(result.contains(booked))
        );
    }

    @Test
    void searchAvailableRoomsTreatsAdjacentBookingsAsAvailable() {
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        storage.addBooking(1, room.getRoomId(), MON, T10, T11, "booked");

        assertAll(
                () -> assertTrue(storage.searchAvailableRooms(MON, T9, T10, 1, features())
                        .contains(room)),
                () -> assertTrue(storage.searchAvailableRooms(MON, T11, T12, 1, features())
                        .contains(room))
        );
    }

    @Test
    void searchAvailableRoomsIgnoresBookingsForOtherRoomsAndDates() {
        Room target = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room other = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        storage.addBooking(1, other.getRoomId(), MON, T9, T10, "other room");
        storage.addBooking(1, target.getRoomId(), MON.plusDays(1), T9, T10, "other day");

        assertTrue(storage.searchAvailableRooms(MON, T9, T10, 1, features()).contains(target));
    }

    @Test
    void searchAvailableRoomsReturnsRoomsSortedById() {
        Room first = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room second = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room third = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        assertEquals(Arrays.asList(first, second, third),
                storage.searchAvailableRooms(MON, T9, T10, 1, features()));
    }

    @Test
    void weeklyScheduleReturnsEmptyListForEmptyStorage() {
        assertTrue(storage.weeklySchedule(1, MON).isEmpty());
    }

    @Test
    void weeklyScheduleFiltersByRoomAndSevenDayWindowThenSorts() {
        User user = storage.addUser("Alice");
        Room target = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room other = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        Booking late = storage.addBooking(user.getUserId(), target.getRoomId(),
                MON, T11, T12, "late");
        Booking early = storage.addBooking(user.getUserId(), target.getRoomId(),
                MON, T9, T10, "early");
        Booking lastDay = storage.addBooking(user.getUserId(), target.getRoomId(),
                MON.plusDays(6), T9, T10, "last day");
        Booking tooLate = storage.addBooking(user.getUserId(), target.getRoomId(),
                MON.plusDays(7), T9, T10, "too late");
        Booking tooEarly = storage.addBooking(user.getUserId(), target.getRoomId(),
                MON.minusDays(1), T9, T10, "too early");
        Booking otherRoom = storage.addBooking(user.getUserId(), other.getRoomId(),
                MON, T9, T10, "other room");

        List<Booking> schedule = storage.weeklySchedule(target.getRoomId(), MON);

        assertEquals(Arrays.asList(early, late, lastDay), schedule);
        assertAll(
                () -> assertFalse(schedule.contains(tooLate)),
                () -> assertFalse(schedule.contains(tooEarly)),
                () -> assertFalse(schedule.contains(otherRoom))
        );
    }

    // ------------------------------------------------------------------
    // Bookings
    // ------------------------------------------------------------------

    @Test
    void addBookingStoresBooking() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.MEETING_ROOM, 10, features());

        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        assertAll(
                () -> assertSame(booking, storage.getBooking(booking.getBookId())),
                () -> assertEquals(user.getUserId(), booking.getUserId()),
                () -> assertEquals(room.getRoomId(), booking.getRoomId()),
                () -> assertEquals(MON, booking.getDate()),
                () -> assertEquals(T9, booking.getStartTime()),
                () -> assertEquals(T10, booking.getEndTime()),
                () -> assertEquals("sync", booking.getPurpose())
        );
    }

    @Test
    void addBookingThrowsWhenRoomDoesNotExist() {
        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.addBooking(1, -1, MON, T9, T10, "sync"));
    }

    @Test
    void addBookingRejectsOverlappingBooking() {
        Room room = storage.addRoom(RoomType.MEETING_ROOM, 10, features());
        storage.addBooking(1, room.getRoomId(), MON, T9, T11, "first");

        assertThrows(CrobsException.BookingConflictException.class,
                () -> storage.addBooking(2, room.getRoomId(), MON, T10, T12, "second"));
    }

    @Test
    void addBookingAllowsAdjacentBooking() {
        Room room = storage.addRoom(RoomType.MEETING_ROOM, 10, features());
        storage.addBooking(1, room.getRoomId(), MON, T9, T10, "first");

        Booking adjacent = storage.addBooking(1, room.getRoomId(), MON, T10, T11, "second");

        assertEquals("second", adjacent.getPurpose());
    }

    @Test
    void getBookingThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class, () -> storage.getBooking(-1));
    }

    @Test
    void getBookingsForUserFiltersAndSorts() {
        User alice = storage.addUser("Alice");
        User bob = storage.addUser("Bob");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        Booking aliceLate = storage.addBooking(alice.getUserId(), room.getRoomId(),
                MON, T11, T12, "late");
        Booking aliceEarly = storage.addBooking(alice.getUserId(), room.getRoomId(),
                MON, T9, T10, "early");
        Booking aliceLaterDate = storage.addBooking(alice.getUserId(), room.getRoomId(),
                MON.plusDays(2), T9, T10, "later date");
        storage.addBooking(bob.getUserId(), room.getRoomId(), MON.plusDays(1), T9, T10, "bob");

        List<Booking> bookings = storage.getBookingsForUser(alice.getUserId());

        assertEquals(Arrays.asList(aliceEarly, aliceLate, aliceLaterDate), bookings);
        assertTrue(storage.getBookingsForUser(-1).isEmpty());
    }

    @Test
    void getAllBookingsReturnsEverythingSorted() {
        User alice = storage.addUser("Alice");
        User bob = storage.addUser("Bob");
        Room firstRoom = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room secondRoom = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());

        Booking later = storage.addBooking(alice.getUserId(), firstRoom.getRoomId(),
                MON, T11, T12, "later");
        Booking earlier = storage.addBooking(bob.getUserId(), secondRoom.getRoomId(),
                MON, T9, T10, "earlier");
        Booking nextDay = storage.addBooking(alice.getUserId(), secondRoom.getRoomId(),
                MON.plusDays(1), T9, T10, "next day");

        assertEquals(Arrays.asList(earlier, later, nextDay), storage.getAllBookings());
    }

    @Test
    void getAllBookingsReturnsEmptyListWhenStorageIsEmpty() {
        assertTrue(storage.getAllBookings().isEmpty());
    }

    @Test
    void removeBookingDeletesBooking() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        storage.removeBooking(booking.getBookId());

        assertThrows(CrobsException.NoSuchBookingException.class,
                () -> storage.getBooking(booking.getBookId()));
    }

    @Test
    void removeBookingThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class, () -> storage.removeBooking(-1));
    }

    @Test
    void changeBookingRoomUpdatesRoom() {
        User user = storage.addUser("Alice");
        Room firstRoom = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room secondRoom = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), firstRoom.getRoomId(),
                MON, T9, T10, "sync");

        storage.changeBookingRoom(booking.getBookId(), secondRoom.getRoomId());

        assertEquals(secondRoom.getRoomId(), storage.getBooking(booking.getBookId()).getRoomId());
    }

    @Test
    void changeBookingRoomThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class,
                () -> storage.changeBookingRoom(-1, 1));
    }

    @Test
    void changeBookingRoomThrowsWhenRoomDoesNotExist() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.changeBookingRoom(booking.getBookId(), -1));
        assertEquals(room.getRoomId(), storage.getBooking(booking.getBookId()).getRoomId());
    }

    @Test
    void changeBookingRoomRejectsConflict() {
        User user = storage.addUser("Alice");
        Room first = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room second = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking moved = storage.addBooking(user.getUserId(), first.getRoomId(),
                MON, T9, T10, "moved");
        storage.addBooking(user.getUserId(), second.getRoomId(),
                MON, LocalTime.of(9, 30), LocalTime.of(10, 30), "blocker");

        assertThrows(CrobsException.BookingConflictException.class,
                () -> storage.changeBookingRoom(moved.getBookId(), second.getRoomId()));
        assertEquals(first.getRoomId(), storage.getBooking(moved.getBookId()).getRoomId());
    }

    @Test
    void changeBookingTimeUpdatesTime() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        storage.changeBookingTime(booking.getBookId(), MON.plusDays(1), T11, T12);

        assertAll(
                () -> assertEquals(MON.plusDays(1), storage.getBooking(booking.getBookId()).getDate()),
                () -> assertEquals(T11, storage.getBooking(booking.getBookId()).getStartTime()),
                () -> assertEquals(T12, storage.getBooking(booking.getBookId()).getEndTime())
        );
    }

    @Test
    void changeBookingTimeRejectsInvalidInterval() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        assertThrows(CrobsException.InvalidValueException.class,
                () -> storage.changeBookingTime(booking.getBookId(), MON, T12, T11));
    }

    @Test
    void changeBookingTimeThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class,
                () -> storage.changeBookingTime(-1, MON, T9, T10));
    }

    @Test
    void changeBookingTimeRejectsConflictWithAnotherBooking() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking first = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "first");
        storage.addBooking(user.getUserId(), room.getRoomId(), MON, T10, T11, "second");

        assertThrows(CrobsException.BookingConflictException.class,
                () -> storage.changeBookingTime(first.getBookId(), MON,
                        LocalTime.of(9, 30), LocalTime.of(10, 30)));
        assertEquals(T10, storage.getBooking(first.getBookId()).getEndTime());
    }

    @Test
    void changeBookingUpdatesEverythingAtOnce() {
        User user = storage.addUser("Alice");
        Room first = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Room second = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), first.getRoomId(),
                MON, T9, T10, "sync");

        storage.changeBooking(booking.getBookId(), second.getRoomId(),
                MON.plusDays(1), T11, T12, "exam");

        assertAll(
                () -> assertEquals(second.getRoomId(),
                        storage.getBooking(booking.getBookId()).getRoomId()),
                () -> assertEquals(MON.plusDays(1), storage.getBooking(booking.getBookId()).getDate()),
                () -> assertEquals(T11, storage.getBooking(booking.getBookId()).getStartTime()),
                () -> assertEquals(T12, storage.getBooking(booking.getBookId()).getEndTime()),
                () -> assertEquals("exam", storage.getBooking(booking.getBookId()).getPurpose())
        );
    }

    @Test
    void changeBookingThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class,
                () -> storage.changeBooking(-1, 1, MON, T9, T10, "exam"));
    }

    @Test
    void changeBookingThrowsWhenRoomDoesNotExist() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        assertThrows(CrobsException.NoSuchRoomException.class,
                () -> storage.changeBooking(booking.getBookId(), -1, MON, T9, T10, "exam"));
        assertEquals(room.getRoomId(), storage.getBooking(booking.getBookId()).getRoomId());
    }

    @Test
    void changeBookingRejectsInvalidInterval() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        assertThrows(CrobsException.InvalidValueException.class,
                () -> storage.changeBooking(booking.getBookId(), room.getRoomId(),
                        MON, T12, T11, "exam"));
        assertEquals(T10, storage.getBooking(booking.getBookId()).getEndTime());
    }

    @Test
    void changeBookingRejectsConflict() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking first = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "first");
        storage.addBooking(user.getUserId(), room.getRoomId(), MON, T10, T11, "second");

        assertThrows(CrobsException.BookingConflictException.class,
                () -> storage.changeBooking(first.getBookId(), room.getRoomId(),
                        MON, LocalTime.of(9, 30), LocalTime.of(10, 30), "exam"));
        assertEquals("first", storage.getBooking(first.getBookId()).getPurpose());
        assertEquals(T9, storage.getBooking(first.getBookId()).getStartTime());
    }

    @Test
    void changeBookingPurposeUpdatesPurpose() {
        User user = storage.addUser("Alice");
        Room room = storage.addRoom(RoomType.SEMINAR_ROOM, 10, features());
        Booking booking = storage.addBooking(user.getUserId(), room.getRoomId(),
                MON, T9, T10, "sync");

        storage.changeBookingPurpose(booking.getBookId(), "exam");

        assertEquals("exam", storage.getBooking(booking.getBookId()).getPurpose());
    }

    @Test
    void changeBookingPurposeThrowsWhenBookingDoesNotExist() {
        assertThrows(CrobsException.NoSuchBookingException.class,
                () -> storage.changeBookingPurpose(-1, "exam"));
    }
}
