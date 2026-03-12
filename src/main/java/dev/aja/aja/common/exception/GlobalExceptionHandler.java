package dev.aja.aja.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.aja.aja.user.exception.UserAlreadyExistException;
import dev.aja.aja.user.exception.UserInvalidRoleException;

/**
 * Aquí implementamos de una manera centralizada toda la gestión de todas las
 * excepciones que queramos capturar. Con esto nos ahorarmos ir haciendo
 * infinidad de try/cath. Si fuera necesario, se podría capturar la excepción
 * más próxima a su ejecución
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Respuesta para la exception UsernameNotFoundException (nombre de usuario) y
     * BadCredentialsException (contraseña) si el usuario no existe o ha introducido
     * mal la contraseña, siempre se responderá esto. Hacemos una respuesta
     * genérica, para que no se pueda averiguar si un usuario existe o no
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de message es un
     *         texto de advertencia
     */
    @ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
    public ResponseEntity<Map<String, Object>> userNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "El usuario al que intentas acceder, no existe"));
    }

    /**
     * Respuesta para la exception UserInvalidRoleException si el usuario que está
     * generando la acción no tiene un role adecuado para ésta
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de message es un
     *         texto de advertencia
     */
    @ExceptionHandler(UserInvalidRoleException.class)
    public ResponseEntity<Map<String, Object>> checkRole() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "No tienes permiso para realizar esta acción"));
    }

    /**
     * Respuesta para la exception UserAlreadyExistException si se está añadiendo un
     * nuevo usuario, se avisará y se cancelará la acción
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de message es un
     *         texto de advertencia
     */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> userExist(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Cuando un usuario está deshabilitado, con isActive = false, se lanza esta
     * excepción
     * 
     * @param e, enviamos la excepción inyectándola como parámetro para obtener el
     *           mensaje
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de message es un
     *         texto de advertencia
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> userIsDisabled(Exception e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", e.getMessage()));
    }
}
