package dev.aja.aja.common.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.aja.aja.forum.exception.ForumAlreadyExistException;
import dev.aja.aja.forum.exception.ForumNotFoundException;
import dev.aja.aja.topic.exception.TopicAlreadyExistException;
import dev.aja.aja.topic.exception.TopicNotFoundException;
import dev.aja.aja.user.exception.EmailAlreadyExistException;
import dev.aja.aja.user.exception.UserAlreadyExistException;
import dev.aja.aja.user.exception.UserInvalidRoleException;
import dev.aja.aja.user.exception.UserInvalidToEditInformation;
import dev.aja.aja.user.exception.UserRoleAlreadyAssignedException;
import dev.aja.aja.user.exception.UsernameAlreadyExistException;

/**
 * Aquí implementamos de una manera centralizada toda la gestión de todas las
 * excepciones que queramos capturar. Con esto nos ahorarmos ir haciendo
 * infinidad de try/cath. Si fuera necesario, se podría capturar la excepción
 * más próxima a su ejecución
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Constructor creado para ignorar warnings cuando se crea javadoc
     */
    public GlobalExceptionHandler() {
    }

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
                .body(Map.of("success", false, "message", "Usuario o contraseña incorrecto"));
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
     * @param e, enviamos la excepción inyectándola como parámetro para obtener el
     *           mensaje
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

    /**
     * Cuando no se permite editar información de un usuario, porque no es un admin
     * o porque no es el propio usuario
     * 
     * @param e, enviamos la excepción inyectándola como parámetro para obtener el
     *           mensaje
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de message es un
     *         texto de advertencia
     */
    @ExceptionHandler(UserInvalidToEditInformation.class)
    public ResponseEntity<Map<String, Object>> userEditError(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Cuando se edita un username o se quiere añadir un nuevo usuario
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> usernameExist(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Cuando se edita un email o se quiere añadir un nuevo usuario
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> emailExist(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Cuando se edita un role que ya lo tiene asignado el usuario
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(UserRoleAlreadyAssignedException.class)
    public ResponseEntity<Map<String, Object>> userRoleAlready(Exception e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * 
     * Al añadir un nuevo foro si este ya existe
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(ForumAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> forumTitleExist(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Al eliminar un forum si este no existe
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(ForumNotFoundException.class)
    public ResponseEntity<Map<String, Object>> forumNotFound(Exception e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Al añadir un topic si ya existe uno con ese título
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(TopicAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> topicFound(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }

    /**
     * Al obtener topic si este no existe
     * 
     * @param e Excepción que aporta el mensaje
     * @return Diccionario con respuesta
     */
    @ExceptionHandler(TopicNotFoundException.class)
    public ResponseEntity<Map<String, Object>> topicNotFound(Exception e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
    }
}
