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

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
}