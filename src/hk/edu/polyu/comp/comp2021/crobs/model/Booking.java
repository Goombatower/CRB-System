package hk.edu.polyu.comp.comp2021.crobs.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A booking session of a room by a user, on a date within a half-open time interval [startTime, endTime).
 *
 * <p>The booking ID and the owner user ID are immutable.</p>
 * The room, date, times and purpose may change through {@code modifyBooking} command.
 */
public final class Booking {

    private final int bookId;
    private final int userId;
    private int roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String purpose;

    /**
     * Creates a booking with the given details.
     *
     * @param bookId the unique ID of this booking
     * @param userId the ID of the user who owns this booking
     * @param roomId the ID of the booked room
     * @param date the date of the booking
     * @param startTime the start time of the booking (inclusive)
     * @param endTime the end time of the booking (exclusive), must be after startTime
     * @param purpose the purpose of the booking, must not be null
     */
    public Booking(int bookId, int userId, int roomId, LocalDate date,
                   LocalTime startTime, LocalTime endTime, String purpose) {
        this.bookId = bookId;
        this.userId = userId;
        setRoomId(roomId);
        setTime(date, startTime, endTime);
        setPurpose(purpose);
    }

    // standard getters
    /**
     * Gets the booking ID.
     *
     * @return the unique ID of this booking
     */
    public int getBookId() {
        return bookId;
    }

    /**
     * Gets the owner's user ID.
     *
     * @return the ID of the user who owns this booking
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the room ID.
     *
     * @return the ID of the booked room
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Gets the booking date.
     *
     * @return the date of the booking
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the start time.
     *
     * @return the start time of the booking (inclusive)
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time.
     *
     * @return the end time of the booking (exclusive)
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the purpose.
     *
     * @return the purpose of the booking
     */
    public String getPurpose() {
        return purpose;
    }

    // setters for constructor and command modifying, with input validation
    /**
     * Sets the booked room.
     *
     * @param roomId the ID of the new room
     */
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    /**
     * Sets the purpose of the booking.
     *
     * @param purpose the new purpose, must not be null
     */
    public void setPurpose(String purpose) {
        this.purpose = Objects.requireNonNull(purpose, "purpose must not be null");
    }

    /**
     * Sets the date and time interval of the booking.
     *
     * @param date the new date, must not be null
     * @param startTime the new start time (inclusive), must not be null
     * @param endTime the new end time (exclusive), must not be null and must be after startTime
     */
    public void setTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        checkTime(date, startTime, endTime);
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Validates a date and time interval without changing this booking.
     *
     * @param date the date to check, must not be null
     * @param startTime the start time to check, must not be null
     * @param endTime the end time to check, must not be null and must be after startTime
     */
    static void checkTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(startTime, "startTime must not be null");
        Objects.requireNonNull(endTime, "endTime must not be null");
        if (!endTime.isAfter(startTime)) {
            throw new CrobsException.InvalidValueException(
                    "endTime must be after startTime: " + startTime + "-" + endTime);
        }
    }

    /**
     * Checks whether another booking conflicts with this one.
     *
     * @param other the booking to compare with, may be null
     * @return true if {@code other} conflicts with this booking, i.e. they refer to
     *         the same room and date and their half-open intervals overlap.
     */
    public boolean overlaps(Booking other) {
        if (other == null) {
            return false;
        }
        if (roomId != other.getRoomId() || !date.equals(other.getDate())) {
            return false;
        }
        // not(non-conflict case1 || case2)，adjacent intervals do not conflict
        return startTime.isBefore(other.getEndTime()) && other.getStartTime().isBefore(endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return bookId == ((Booking) o).getBookId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookId);
    }

    @Override
    public String toString() {
        return "Booking{bookId=" + bookId + ", user=" + userId + ", room=" + roomId
                + ", " + date + " " + startTime + "-" + endTime
                + ", purpose='" + purpose + "'}";
    }
}
