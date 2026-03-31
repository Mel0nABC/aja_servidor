package dev.aja.aja.user.exception;

/**
 * Exception para indicar el role del usuario no era el adecuado
 */
public class UserInvalidToEditInformation extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UserInvalidToEditInformation() {
        super("Not have permissions for edit information from other user");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UserInvalidToEditInformation(String message) {
        super(message);
    }

}
