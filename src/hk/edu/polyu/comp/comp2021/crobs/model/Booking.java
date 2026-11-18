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

    public Booking(int bookId, int userId, int roomId, LocalDate date,
                   LocalTime startTime, LocalTime endTime, String purpose) {
        this.bookId = bookId;
        this.userId = userId;
        setRoomId(roomId);
        setTime(date, startTime, endTime);
        setPurpose(purpose);
    }

    // standard getters
    public int getBookId() {
        return bookId;
    }
    public int getUserId() {
        return userId;
    }
    public int getRoomId() {
        return roomId;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public String getPurpose() {
        return purpose;
    }

    // setters for constructor and command modifying, with input validation
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public void setPurpose(String purpose) {
        this.purpose = Objects.requireNonNull(purpose, "purpose must not be null");
    }
    public void setTime(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(startTime, "startTime must not be null");
        Objects.requireNonNull(endTime, "endTime must not be null");
        if (!endTime.isAfter(startTime)) {
             throw new CrobsException.InvalidValueException(
                   "endTime must be after startTime: " + startTime + "-" + endTime);
        }
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * @return true if {@code other} conflicts with this booking, i.e. they refer to
     *         the same room and date and their half-open intervals overlap.
     */
    public boolean overlaps(Booking other) {
        if (other == null) {
            return false;
        }
        if (roomId != other.roomId || !date.equals(other.date)) {
            return false;
        }
        // not(non-conflict case1 || case2)，adjacent intervals do not conflict
        return startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Booking)) {
            return false;
        }
        return bookId == ((Booking) o).bookId;
    }

    @Override
    public String toString() {
        return "Booking{bookId=" + bookId + ", user=" + userId + ", room=" + roomId
                + ", " + date + " " + startTime + "-" + endTime
                + ", purpose='" + purpose + "'}";
    }
}