package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.UserType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTypeTest {

    @Test
    void fromTokenResolvesEveryRoleIgnoringCase() {
        assertAll(
                () -> assertSame(UserType.REGULAR, UserType.fromToken("user")),
                () -> assertSame(UserType.REGULAR, UserType.fromToken("USER")),
                () -> assertSame(UserType.ADMIN, UserType.fromToken("admin")),
                () -> assertSame(UserType.ADMIN, UserType.fromToken("Admin")),
                () -> assertSame(UserType.SUPERADMIN, UserType.fromToken("superadmin")),
                () -> assertSame(UserType.SUPERADMIN, UserType.fromToken("SuPeRaDmIn"))
        );
    }

    @Test
    void fromTokenRejectsNull() {
        assertThrows(CrobsException.InvalidValueException.class, () -> UserType.fromToken(null));
    }

    @Test
    void fromTokenRejectsUnknownRole() {
        assertThrows(CrobsException.InvalidValueException.class,
                () -> UserType.fromToken("guest"));
    }

    @Test
    void valuesAndValueOfExposeTheConstants() {
        assertAll(
                () -> assertEquals(3, UserType.values().length),
                () -> assertSame(UserType.ADMIN, UserType.valueOf("ADMIN"))
        );
    }
}
