package dev.aja.aja.topic.exception;

/**
 * Exception para indicar que el topic ya existe
 */
public class TopicAlreadyExistException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public TopicAlreadyExistException() {
        super("Topic already exist");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public TopicAlreadyExistException(String message) {
        super(message);
    }
}
