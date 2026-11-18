package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.Booking;
import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingTest {

    private static final LocalDate DATE = LocalDate.of(2026, 3, 2);
    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(10, 30);

    private static Booking booking() {
        return new Booking(1, 2, 3, DATE, START, END, "lecture");
    }

    @Test
    void constructorStoresEveryField() {
        Booking booking = booking();
        assertAll(
                () -> assertEquals(1, booking.getBookId()),
                () -> assertEquals(2, booking.getUserId()),
                () -> assertEquals(3, booking.getRoomId()),
                () -> assertEquals(DATE, booking.getDate()),
                () -> assertEquals(START, booking.getStartTime()),
                () -> assertEquals(END, booking.getEndTime()),
                () -> assertEquals("lecture", booking.getPurpose())
        );
    }

    @Test
    void settersUpdateMutableFields() {
        Booking booking = booking();
        booking.setRoomId(17);
        booking.setPurpose("exam");
        booking.setTime(DATE.plusDays(1), LocalTime.of(13, 0), LocalTime.of(14, 0));

        assertAll(
                () -> assertEquals(17, booking.getRoomId()),
                () -> assertEquals("exam", booking.getPurpose()),
                () -> assertEquals(DATE.plusDays(1), booking.getDate()),
                () -> assertEquals(LocalTime.of(13, 0), booking.getStartTime()),
                () -> assertEquals(LocalTime.of(14, 0), booking.getEndTime())
        );
    }

    @Test
    void setTimeRejectsNullArguments() {
        Booking booking = booking();
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> booking.setTime(null, START, END)),
                () -> assertThrows(NullPointerException.class,
                        () -> booking.setTime(DATE, null, END)),
                () -> assertThrows(NullPointerException.class,
                        () -> booking.setTime(DATE, START, null))
        );
    }

    @Test
    void setTimeRejectsEndNotAfterStart() {
        Booking booking = booking();
        assertAll(
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> booking.setTime(DATE, START, START)),
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> booking.setTime(DATE, END, START))
        );
    }

    @Test
    void setPurposeRejectsNull() {
        Booking booking = booking();
        assertThrows(NullPointerException.class, () -> booking.setPurpose(null));
    }

    @Test
    void overlapsReturnsFalseForNull() {
        assertFalse(booking().overlaps(null));
    }

    @Test
    void overlapsReturnsFalseForDifferentRoomOrDate() {
        Booking booking = booking();
        Booking differentRoom = new Booking(2, 2, 4, DATE, START, END, "lecture");
        Booking differentDate = new Booking(3, 2, 3, DATE.plusDays(1), START, END, "lecture");

        assertAll(
                () -> assertFalse(booking.overlaps(differentRoom)),
                () -> assertFalse(booking.overlaps(differentDate))
        );
    }

    @Test
    void overlapsDetectsOverlappingIntervals() {
        Booking booking = booking();
        Booking overlapping = new Booking(2, 2, 3, DATE, LocalTime.of(10, 0), LocalTime.of(11, 0),
                "lecture");

        assertTrue(booking.overlaps(overlapping));
    }

    @Test
    void overlapsReturnsFalseForAdjacentIntervals() {
        Booking booking = booking();
        Booking adjacent = new Booking(2, 2, 3, DATE, END, LocalTime.of(12, 0), "lecture");

        assertFalse(booking.overlaps(adjacent));
    }

    @Test
    void overlapsReturnsFalseWhenOtherBookingIsEarlier() {
        Booking booking = booking();
        Booking earlier = new Booking(2, 2, 3, DATE, LocalTime.of(7, 0), START, "lecture");

        assertFalse(booking.overlaps(earlier));
    }

    @Test
    void equalsComparesBookIdOnly() {
        Booking booking = booking();
        Booking sameId = new Booking(1, 99, 99, DATE.plusDays(5), LocalTime.of(15, 0),
                LocalTime.of(16, 0), "other");
        Booking differentId = new Booking(2, 2, 3, DATE, START, END, "lecture");

        assertAll(
                () -> assertTrue(booking.equals(booking)),
                () -> assertTrue(booking.equals(sameId)),
                () -> assertFalse(booking.equals(differentId)),
                () -> assertFalse(booking.equals("not a booking")),
                () -> assertFalse(booking.equals(null))
        );
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("Booking{bookId=1, user=2, room=3, 2026-03-02 09:00-10:30, purpose='lecture'}",
                booking().toString());
    }
}
