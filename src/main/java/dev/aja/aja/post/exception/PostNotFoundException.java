package dev.aja.aja.post.exception;

/**
 * Exception para indicar que el post no existe
 */
public class PostNotFoundException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public PostNotFoundException() {
        super("Post not found");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public PostNotFoundException(String message) {
        super(message);
    }

}
