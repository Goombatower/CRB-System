package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.IdGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdGeneratorTest {

    @Test
    void eachGeneratorAdvancesByOne() {
        int firstUser = IdGenerator.nextUserId();
        int secondUser = IdGenerator.nextUserId();
        int firstRoom = IdGenerator.nextRoomId();
        int secondRoom = IdGenerator.nextRoomId();
        int firstBooking = IdGenerator.nextBookId();
        int secondBooking = IdGenerator.nextBookId();

        assertAll(
                () -> assertEquals(firstUser + 1, secondUser),
                () -> assertEquals(firstRoom + 1, secondRoom),
                () -> assertEquals(firstBooking + 1, secondBooking)
        );
    }

    @Test
    void privateConstructorCanBeInvokedReflectively() throws Exception {
        Constructor<IdGenerator> constructor = IdGenerator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
