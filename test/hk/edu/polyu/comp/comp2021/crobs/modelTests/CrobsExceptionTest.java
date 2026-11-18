package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class CrobsExceptionTest {

    @Test
    void messageConstructorStoresMessage() {
        CrobsException exception = new CrobsException("boom");

        assertAll(
                () -> assertEquals("boom", exception.getMessage()),
                () -> assertEquals(null, exception.getCause())
        );
    }

    @Test
    void messageAndCauseConstructorStoresBoth() {
        RuntimeException cause = new RuntimeException("root");
        CrobsException exception = new CrobsException("boom", cause);

        assertAll(
                () -> assertEquals("boom", exception.getMessage()),
                () -> assertSame(cause, exception.getCause())
        );
    }

    @Test
    void allNestedExceptionsCarryTheirMessage() {
        assertAll(
                () -> assertNested(new CrobsException.InvalidCommandException("bad command"), "bad command"),
                () -> assertNested(new CrobsException.PermissionDeniedException("denied"), "denied"),
                () -> assertNested(new CrobsException.NoSuchUserException("no user"), "no user"),
                () -> assertNested(new CrobsException.NoSuchRoomException("no room"), "no room"),
                () -> assertNested(new CrobsException.NoSuchBookingException("no booking"), "no booking"),
                () -> assertNested(new CrobsException.BookingConflictException("conflict"), "conflict"),
                () -> assertNested(new CrobsException.InvalidValueException("invalid"), "invalid")
        );
    }

    private static void assertNested(CrobsException exception, String message) {
        assertEquals(message, exception.getMessage());
        assertInstanceOf(CrobsException.class, exception);
    }
}
