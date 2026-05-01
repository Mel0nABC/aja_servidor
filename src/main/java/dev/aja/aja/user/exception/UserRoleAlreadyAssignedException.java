package dev.aja.aja.user.exception;

/**
 * Exception para indicar que el nuevo role ya es el que tiene el usuario
 */
public class UserRoleAlreadyAssignedException extends RuntimeException {

    /**
     * Constructor predeterminado
     */
    public UserRoleAlreadyAssignedException() {
        super("El usuario ya tiene ese role");
    }

    /**
     * Constructor que se le indica qué mensaje se quiere tener en la excepción
     * 
     * @param message texto del mensaje que se quiere tener
     */
    public UserRoleAlreadyAssignedException(String message) {
        super(message);
    }

}
