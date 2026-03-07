package dev.aja.aja.auth.exception;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException() {
        super("User already existe");
    }

    public UserAlreadyExistException(String message) {
        super(message);
    }

}
