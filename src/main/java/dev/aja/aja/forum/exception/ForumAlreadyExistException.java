package dev.aja.aja.forum.exception;

/**
 * Exception para indicar que el forum ya existe
 */
public class ForumAlreadyExistException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public ForumAlreadyExistException() {
        super("Forum already exist");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public ForumAlreadyExistException(String message) {
        super(message);
    }
}
