package dev.aja.aja.forum.exception;

/**
 * Exception para indicar que el forum no existe
 */
public class ForumNotFoundException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public ForumNotFoundException() {
        super("Forum not found");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public ForumNotFoundException(String message) {
        super(message);
    }
}
