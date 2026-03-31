package dev.aja.aja.user.exception;

/**
 * Exception para indicar el role del usuario no era el adecuado
 */
public class UsernameAlreadyExistException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UsernameAlreadyExistException() {
        super("Username already exist");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UsernameAlreadyExistException(String message) {
        super(message);
    }

}
