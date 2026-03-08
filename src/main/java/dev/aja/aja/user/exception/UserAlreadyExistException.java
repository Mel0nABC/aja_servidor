package dev.aja.aja.user.exception;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException() {
        super("User already existe");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

}
