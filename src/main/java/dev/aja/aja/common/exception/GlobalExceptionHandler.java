package dev.aja.aja.common.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        return ResponseEntity
                .ok(Map.of("success", false, "message", "El usuario al que intentas acceder, no existe"));
    }
}
