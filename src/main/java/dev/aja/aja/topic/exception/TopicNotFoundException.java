package dev.aja.aja.topic.exception;

/**
 * Exception para indicar que el topic no existe
 */
public class TopicNotFoundException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public TopicNotFoundException() {
        super("Topic already exist");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public TopicNotFoundException(String message) {
        super(message);
    }
}
