package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.User;

import java.util.Optional;

/**
 * The login state of the current Crobs session.
 *
 * <p>At the start of a session no user is logged in. {@code login} and
 * {@code logout} changes this state.</p>
 */
public final class Session {

    /** The logged-in user, or empty while nobody is logged in. */
    private Optional<User> currentUser = Optional.empty();

    /**
     * @return the user of the session, or {@code null} if nobody is logged in
     */
    public User getCurrentUser() {
        return currentUser.orElse(null);
    }

    /**
     * @param user changes the current user to the param user
     */
    public void setCurrentUser(User user) {
        this.currentUser = Optional.ofNullable(user);
    }

    /**
     * @return checks if someone is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser.isPresent();
    }

    /**
     * clears the current user, ending the login
     */
    public void logout() {
        this.currentUser = Optional.empty();
    }
}
