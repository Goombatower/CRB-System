package hk.edu.polyu.comp.comp2021.crobs.controller;

import hk.edu.polyu.comp.comp2021.crobs.model.User;

/**
 * The login state of the current Crobs session.
 *
 * <p>At the start of a session no user is logged in. {@code login} and
 * {@code logout} changes this state.</p>
 */
public final class Session {

    private User currentUser;

    /**
     * @return gets the user of the session
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * @param user changes the current user to the param user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * @return checks if someone is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * sets the current user to null
     */
    public void logout() {
        this.currentUser = null;
    }
}
