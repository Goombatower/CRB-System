package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Room {
    // final id
    private final int roomId;
    private RoomType roomType;
    private int capacity;
    // never change its reference
    private final Set<String> features = new LinkedHashSet<>();

    public Room(int roomId, RoomType roomType, int capacity, Collection<String> features) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.capacity = capacity;
        this.features.addAll(features);
    }

    public int getId() {
        return roomId;
    }

    public RoomType getType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public Set<String> getFeatures() {
        // only return view, make it unmodifiable
        return Collections.unmodifiableSet(features);
    }

    // may be used by id generator ?
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Room)) {
            return false;
        }
        return roomId == ((Room) o).roomId;
    }

//    @Override
//    public int hashCode() {
//        return Objects.hash(roomId);
//    }

    @Override
    public String toString() {
        return "Room{id=" + roomId + ", type=" + roomType.getTokenType()
                + ", capacity=" + capacity + ", features=" + features + "}";
    }
}
