package dev.aja.aja.user.exception;

/**
 * Exception para indicar que el usuario que se intenta registrar ya existe
 */
public class UserAlreadyExistException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UserAlreadyExistException() {
        super("User already existe");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UserAlreadyExistException(String message) {
        super(message);
    }

}
