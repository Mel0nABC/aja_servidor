package dev.aja.aja.directmessage.exception;

/**
 * Exception para indicar el usuario destino de un directmessage no existe
 */
public class UserDestinationNotFoundExcepcion extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UserDestinationNotFoundExcepcion() {
        super("Destination user not found");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UserDestinationNotFoundExcepcion(String message) {
        super(message);
    }

}
