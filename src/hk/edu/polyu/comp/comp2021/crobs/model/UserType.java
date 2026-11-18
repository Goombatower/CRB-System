package hk.edu.polyu.comp.comp2021.crobs.model;

/// The Usertypes. Decided against class implementation of users for easier Command implementation
public enum UserType {
    /** A regular user: books rooms and views schedules. */
    REGULAR,
    /** An admin user: additionally manages rooms and regular users. */
    ADMIN,
    /** The unique superadmin: the only UserType allowed to change UserType. */
    SUPERADMIN;

    /**
     *
     * @param token takes in the enum token
     * @return returns the string form of the token
     */
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
