package dev.aja.aja.user.exception;

public class UserInvalidRoleException extends RuntimeException {

    public UserInvalidRoleException() {
        super("Invalid role exception");
    }

    public UserInvalidRoleException(String message) {
        super(message);
    }

}
