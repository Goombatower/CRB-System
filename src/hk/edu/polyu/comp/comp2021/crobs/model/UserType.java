package hk.edu.polyu.comp.comp2021.crobs.model;

public enum UserType {
    /** A regular user: books rooms and views schedules. */
    REGULAR,
    /** An admin user: additionally manages rooms and regular users. */
    ADMIN,
    /** The unique superadmin: the only UserType allowed to change UserType. */
    SUPERADMIN;

    public static UserType fromToken(String token) {
        if (token == null) {
            throw new CrobsException.InvalidValueException("user type is missing");
        }
        return switch (token.toLowerCase()) {
            case "user" -> REGULAR;
            case "admin" -> ADMIN;
            case "superadmin" -> SUPERADMIN;
            default -> throw new CrobsException.InvalidValueException("unknown user type: " + token);
        };
    }
}
