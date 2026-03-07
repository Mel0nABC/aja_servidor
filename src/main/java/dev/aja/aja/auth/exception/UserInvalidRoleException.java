package dev.aja.aja.auth.exception;

public class UserInvalidRoleException extends RuntimeException {

    public UserInvalidRoleException() {
        super("Invalid role exception");
    }

    public UserInvalidRoleException(String message) {
        super(message);
    }

}
