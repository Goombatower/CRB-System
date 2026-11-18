package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.User;
import hk.edu.polyu.comp.comp2021.crobs.model.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void constructorStoresEveryField() {
        User user = new User(1, "Alice", UserType.REGULAR);

        assertAll(
                () -> assertEquals(1, user.getUserId()),
                () -> assertEquals("Alice", user.getName()),
                () -> assertEquals(UserType.REGULAR, user.getUserType())
        );
    }

    @Test
    void constructorRejectsNullNameAndType() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, null, UserType.REGULAR)),
                () -> assertThrows(NullPointerException.class,
                        () -> new User(1, "Alice", null))
        );
    }

    @Test
    void setUserTypeUpdatesTypeAndRejectsNull() {
        User user = new User(1, "Alice", UserType.REGULAR);
        user.setUserType(UserType.ADMIN);

        assertAll(
                () -> assertEquals(UserType.ADMIN, user.getUserType()),
                () -> assertThrows(NullPointerException.class, () -> user.setUserType(null))
        );
    }

    @Test
    void isAdminRecognisesAdminAndSuperAdmin() {
        assertAll(
                () -> assertTrue(new User(1, "A", UserType.ADMIN).isAdmin()),
                () -> assertTrue(new User(2, "S", UserType.SUPERADMIN).isAdmin()),
                () -> assertFalse(new User(3, "R", UserType.REGULAR).isAdmin())
        );
    }

    @Test
    void isSuperAdminRecognisesOnlySuperAdmin() {
        assertAll(
                () -> assertTrue(new User(1, "S", UserType.SUPERADMIN).isSuperAdmin()),
                () -> assertFalse(new User(2, "A", UserType.ADMIN).isSuperAdmin()),
                () -> assertFalse(new User(3, "R", UserType.REGULAR).isSuperAdmin())
        );
    }

    @Test
    void equalsComparesUserIdOnly() {
        User user = new User(1, "Alice", UserType.REGULAR);
        User sameId = new User(1, "Bob", UserType.ADMIN);
        User differentId = new User(2, "Alice", UserType.REGULAR);

        assertAll(
                () -> assertTrue(user.equals(user)),
                () -> assertTrue(user.equals(sameId)),
                () -> assertFalse(user.equals(differentId)),
                () -> assertFalse(user.equals("not a user")),
                () -> assertFalse(user.equals(null)),
                () -> assertEquals(user.hashCode(), sameId.hashCode()),
                () -> assertNotEquals(user.hashCode(), differentId.hashCode())
        );
    }

    @Test
    void toStringContainsAllFields() {
        assertEquals("User{id=1, name='Alice', role=REGULAR}",
                new User(1, "Alice", UserType.REGULAR).toString());
    }
}
