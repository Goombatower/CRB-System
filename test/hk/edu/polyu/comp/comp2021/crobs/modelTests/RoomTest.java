package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.Room;
import hk.edu.polyu.comp.comp2021.crobs.model.RoomType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {

    private static Set<String> features(String... features) {
        return new LinkedHashSet<>(Arrays.asList(features));
    }

    private static Room room() {
        return new Room(1, RoomType.SEMINAR_ROOM, 20, features("projector", "whiteboard"));
    }

    @Test
    void constructorStoresEveryField() {
        Room room = room();

        assertAll(
                () -> assertEquals(1, room.getRoomId()),
                () -> assertEquals(RoomType.SEMINAR_ROOM, room.getRoomType()),
                () -> assertEquals(20, room.getCapacity()),
                () -> assertEquals(features("projector", "whiteboard"), room.getFeatures())
        );
    }

    @Test
    void constructorRejectsNullType() {
        assertThrows(NullPointerException.class,
                () -> new Room(1, null, 20, features()));
    }

    @Test
    void constructorRejectsNonPositiveCapacity() {
        assertAll(
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> new Room(1, RoomType.SEMINAR_ROOM, 0, features())),
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> new Room(1, RoomType.SEMINAR_ROOM, -5, features()))
        );
    }

    @Test
    void featuresViewIsUnmodifiable() {
        Room room = room();

        assertThrows(UnsupportedOperationException.class, () -> room.getFeatures().add("tv"));
    }

    @Test
    void setTypeUpdatesTypeAndRejectsNull() {
        Room room = room();
        room.setType(RoomType.MEETING_ROOM);

        assertAll(
                () -> assertEquals(RoomType.MEETING_ROOM, room.getRoomType()),
                () -> assertThrows(NullPointerException.class, () -> room.setType(null))
        );
    }

    @Test
    void setCapacityUpdatesCapacityAndRejectsNonPositive() {
        Room room = room();
        room.setCapacity(35);

        assertAll(
                () -> assertEquals(35, room.getCapacity()),
                () -> assertThrows(CrobsException.InvalidValueException.class, () -> room.setCapacity(0)),
                () -> assertThrows(CrobsException.InvalidValueException.class, () -> room.setCapacity(-1))
        );
    }

    @Test
    void setFeaturesReplacesExistingFeatures() {
        Room room = room();
        room.setFeatures(features("tv"));

        assertEquals(features("tv"), room.getFeatures());
    }

    @Test
    void setFeaturesCanClearAllFeatures() {
        Room room = room();
        room.setFeatures(new ArrayList<>());

        assertTrue(room.getFeatures().isEmpty());
    }

    @Test
    void equalsComparesRoomIdOnly() {
        Room room = room();
        Room sameId = new Room(1, RoomType.LECTURE_THEATRE, 100, features("tv"));
        Room differentId = new Room(2, RoomType.SEMINAR_ROOM, 20, features("projector", "whiteboard"));

        assertAll(
                () -> assertTrue(room.equals(room)),
                () -> assertTrue(room.equals(sameId)),
                () -> assertFalse(room.equals(differentId)),
                () -> assertFalse(room.equals("not a room")),
                () -> assertFalse(room.equals(null)),
                () -> assertEquals(room.hashCode(), sameId.hashCode()),
                () -> assertNotEquals(room.hashCode(), differentId.hashCode())
        );
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("Room{id=1, type=seminarRoom, capacity=20, features=projector,whiteboard}",
                room().toString());
    }
}
