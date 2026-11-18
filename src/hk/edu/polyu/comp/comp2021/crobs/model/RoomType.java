package hk.edu.polyu.comp.comp2021.crobs.model;


/**
 * The Room types. Decided against class implementation of users for easier Command implementation
 */
public enum RoomType {
    /**
     * the 4 basic types of rooms
     */
    LECTURE_THEATRE("lectureTheatre"),
    /**
     *
     */
    COMPUTER_LAB("computerLab"),
    /**
     *
     */
    SEMINAR_ROOM("seminarRoom"),
    /**
     * don't know why we need 4 seperate comments
     */
    MEETING_ROOM("meetingRoom");

    // this tokenType is used to be displayed in command line
    private final String tokenType;

    RoomType (String type){
        this.tokenType = type;
    }

    /**
     * @return returns the string version of the token
     */
    public String getTokenType() {
        return this.tokenType;
    }

    /**
     * @param token intakes the string
     * @return outputs the type of token in enum
     */
    public static RoomType fromToken (String token) {
        if (token == null) {
            throw new CrobsException.InvalidValueException("missing room type");
        }
        for (RoomType type : values()) {
            if (type.getTokenType().equals(token)) {
                return type;
            }
        }
        throw new CrobsException.InvalidValueException("unknown room type: " + token);
    }
}
