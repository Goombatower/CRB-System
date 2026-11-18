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

    public User(int userId, String name, UserType userType) {
        this.userId = userId;
        this.name = Objects.requireNonNull(name, "name must not be null");
        setUserType(userType);
    }

    // standard getters
    public int getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public UserType getUserType() {
        return userType;
    }

    // setters for constructor and command modifying, with input validation
    public void setUserType(UserType userType) {
        this.userType = Objects.requireNonNull(userType, "role must not be null");
    }

    /** @return true if this user has admin (or superadmin) privileges. */
    public boolean isAdmin() {
        return userType == UserType.ADMIN || userType == UserType.SUPERADMIN;
    }
    /** @return true if this user is the superadmin. */
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
        return userId == ((User) o).userId;
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + name + "', role=" + userType + "}";
    }
}
