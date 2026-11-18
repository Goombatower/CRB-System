package hk.edu.polyu.comp.comp2021.crobs.modelTests;

import hk.edu.polyu.comp.comp2021.crobs.model.CrobsException;
import hk.edu.polyu.comp.comp2021.crobs.model.RoomType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomTypeTest {

    @Test
    void everyConstantHasItsToken() {
        assertAll(
                () -> assertEquals("lectureTheatre", RoomType.LECTURE_THEATRE.getTokenType()),
                () -> assertEquals("computerLab", RoomType.COMPUTER_LAB.getTokenType()),
                () -> assertEquals("seminarRoom", RoomType.SEMINAR_ROOM.getTokenType()),
                () -> assertEquals("meetingRoom", RoomType.MEETING_ROOM.getTokenType())
        );
    }

    @Test
    void fromTokenResolvesEveryConstant() {
        assertAll(
                () -> assertSame(RoomType.LECTURE_THEATRE, RoomType.fromToken("lectureTheatre")),
                () -> assertSame(RoomType.COMPUTER_LAB, RoomType.fromToken("computerLab")),
                () -> assertSame(RoomType.SEMINAR_ROOM, RoomType.fromToken("seminarRoom")),
                () -> assertSame(RoomType.MEETING_ROOM, RoomType.fromToken("meetingRoom"))
        );
    }

    @Test
    void fromTokenRejectsNull() {
        assertThrows(CrobsException.InvalidValueException.class, () -> RoomType.fromToken(null));
    }

    @Test
    void fromTokenRejectsUnknownToken() {
        assertAll(
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> RoomType.fromToken("lectureHall")),
                () -> assertThrows(CrobsException.InvalidValueException.class,
                        () -> RoomType.fromToken("SEMINARROOM"))
        );
    }

    @Test
    void valuesAndValueOfExposeTheConstants() {
        assertAll(
                () -> assertEquals(4, RoomType.values().length),
                () -> assertSame(RoomType.COMPUTER_LAB, RoomType.valueOf("COMPUTER_LAB"))
        );
    }
}
