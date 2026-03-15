package dev.aja.aja.user.exception;

/**
 * Exception para indicar el role del usuario no era el adecuado
 */
public class UserInvalidRoleException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UserInvalidRoleException() {
        super("Invalid role exception");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UserInvalidRoleException(String message) {
        super(message);
    }

}
