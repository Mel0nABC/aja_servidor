package dev.aja.aja.directmessage.exception;

/**
 * Exception para indicar el usuario destino es el mismo que el de origen
 */
public class SameUserException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public SameUserException() {
        super("Destination user is same");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public SameUserException(String message) {
        super(message);
    }

}
