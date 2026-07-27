package controller;

import model.User;

/** Outcome of an {@link AuthenticationController#login(String, String)} attempt. */
public class AuthResult {

    private final boolean success;
    private final String message;
    private final User user;

    public AuthResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
