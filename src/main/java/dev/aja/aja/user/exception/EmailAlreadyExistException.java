package dev.aja.aja.user.exception;

/**
 * Exception para indicar el role del usuario no era el adecuado
 */
public class EmailAlreadyExistException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public EmailAlreadyExistException() {
        super("Email already exist");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public EmailAlreadyExistException(String message) {
        super(message);
    }

}
