package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A room. Every room has a unique roomId, a {@link RoomType}, a capacity and a features.
 *
 * <p>The roomId is immutable.</p>
 * The RoomType, capacity and features may change through admin's {@code modifyRoom} command.
 */
public final class Room {
    private final int roomId;
    private RoomType roomType;
    private int capacity;
    // never change its reference, only update data inside
    private final Set<String> features = new LinkedHashSet<>();

    public Room(int roomId, RoomType roomType, int capacity, Collection<String> features) {
        // idGenerator should not be used here, it should be used by class who is creating instance
        this.roomId = roomId;
        setType(roomType);
        setCapacity(capacity);
        setFeatures(features);
    }

    // standard getters
    public int getRoomId() {
        return roomId;
    }
    public RoomType getRoomType() {
        return roomType;
    }
    public int getCapacity() {
        return capacity;
    }
    public Set<String> getFeatures() {
        // only return view, make it unmodifiable
        return Collections.unmodifiableSet(features);
    }

    // setters for constructor and command modifying, with input validation
    public void setType(RoomType type) {
        this.roomType = Objects.requireNonNull(type, "type must not be null");
    }
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new CrobsException.InvalidValueException("capacity must be positive: " + capacity);
        }
        this.capacity = capacity;
    }
    public void setFeatures(Collection<String> features) {
        this.features.clear();
        this.features.addAll(features);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return roomId == ((Room) o).getroomId();
    }

    @Override
    public String toString() {
        return "Room{id=" + roomId + ", type=" + roomType.getTokenType()
                + ", capacity=" + capacity + ", features="
                + String.join(",", features) + "}";
    }
}
