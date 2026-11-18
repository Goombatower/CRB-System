package hk.edu.polyu.comp.comp2021.crobs.model;

import java.util.Objects;

/**
 * A Crobs user. Every user has a unique userId, a name, and a {@link UserType}.
 *
 * <p>The userId and name are immutable.</p>
 * The UserType may change through superadmin's {@code changeRole} command.
 */
public final class User {
    private final int userId;
    private final String name;
    private UserType userType;

    /**
     * Creates a user with the given details.
     *
     * @param userId the unique ID of the user
     * @param name the name of the user, must not be null
     * @param userType the type of the user, must not be null
     */
    public User(int userId, String name, UserType userType) {
        this.userId = userId;
        this.name = Objects.requireNonNull(name, "name must not be null");
        setUserType(userType);
    }

    // standard getters
    /**
     * Gets the user ID
     * @return the unique ID of this user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Gets the user's name.
     * @return the name of this user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's type.
     * @return the current type of this user
     */
    public UserType getUserType() {
        return userType;
    }

    // setters for constructor and command modifying, with input validation
    /**
     * Sets the user's type.
     * @param userType the new type of this user, must not be null
     */
    public void setUserType(UserType userType) {
        this.userType = Objects.requireNonNull(userType, "role must not be null");
    }

    /**
     * Checks whether this user has admin privileges.
     * @return true if this user has admin (or superadmin) privileges.
     */
    public boolean isAdmin() {
        return userType == UserType.ADMIN || userType == UserType.SUPERADMIN;
    }

    /**
     * Checks whether this user is the superadmin.
     * @return true if this user is the superadmin.
     */
    public boolean isSuperAdmin() {
        return userType == UserType.SUPERADMIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return userId == ((User) o).getUserId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(userId);
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + name + "', role=" + userType + "}";
    }
}
