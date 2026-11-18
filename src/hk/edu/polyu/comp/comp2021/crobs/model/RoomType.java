package hk.edu.polyu.comp.comp2021.crobs.model;

public enum RoomType {
    LECTURE_THEATRE("lectureTheatre"),
    COMPUTER_LAB("computerLab"),
    SEMINAR_ROOM("seminarRoom"),
    MEETING_ROOM("meetingRoom");

    // this tokenType is used to be displayed in command line
    private final String tokenType;

    RoomType (String type){
        this.tokenType = type;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public static RoomType fromToken (String token) {
        if (token == null) {
            throw new CrobsException.InvalidValueException("missing room type");
        }
        for (RoomType type : values()) {
            if (type.tokenType.equals(token)) {
                return type;
            }
        }
        throw new CrobsException.InvalidValueException("unknown room type: " + token);
    }
}
