package dev.aja.aja.directmessage.exception;

/**
 * Exception para indicar que en con usuario destino no existe una conversación
 */
public class DirectMessageNotFoundException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public DirectMessageNotFoundException() {
        super("Conversation not found");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public DirectMessageNotFoundException(String message) {
        super(message);
    }

}
