package dev.aja.aja.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.aja.aja.auth.entity.UserEntity;
import dev.aja.aja.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Clase que nos va a gestionar todos los endpoints referentes a authenicación,
 * vease login, logout, etc
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor para inyectar dependencias necesarias
     * 
     * @param authService service para acceder a datos de la bbdd y posibles métodos
     *                    de la unidad de negocio
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Método que es el endpoint health, simplemente para comprobar que el servidor
     * está activo
     * 
     * @return
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> isServerAlive() {
        return ResponseEntity.ok(Map.of("success", true, "message", "El servidor está activo"));
    }

    /**
     * Login, para poder realizar petición de login a la aplicación
     * 
     * @param username nombre de usuario para hacer la petición de login
     * @param password contraseña de usuario para hacer la petición de login
     * @param request  información de la petición http, headers, body, etcétera.
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un UserEntityDTO con la información del usuario que acaba de
     *         logear
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password,
            HttpServletRequest request) {
        return ResponseEntity
                .ok(Map.of("success", true, "message", authService.login(username, password, request).toDTO()));
    }

    /**
     * Logout, para poder realizar petición de logout a la aplicación
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un boolean, que nos indica si ha sido correcta o o no la acción,
     *         con true o false respectivamente
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        return ResponseEntity.ok(Map.of("success", true, "message", authService.logout()));
    }

    /**
     * 
     * Añadir usuario, este endpoint está filtrado sólo para role admin
     * 
     * @param userEntity hay que recibir un UserEntity desde el cliente.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserEntity userEntity) {

        authService.addUser(userEntity);

        return ResponseEntity.ok(Map.of("success", true, "message", "Usuario añadido satisfactoriamente"));
    }

    /**
     * 
     * Añadir usuario, este endpoint está filtrado sólo para role admin
     * 
     * @param userEntity hay que recibir un UserEntity desde el cliente.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PutMapping("/user")
    public ResponseEntity<Map<String, Object>> editUser(@RequestBody UserEntity userEntity) {

        authService.editUser(userEntity);

        return ResponseEntity.ok(Map.of("success", true, "message", "Usurio editado satisfactoriamente"));
    }

    /**
     * 
     * Eliminar usuario, este endpoint está filtrado sólo para role admin
     * 
     * @param id identificado del usuario que se va a eliminar
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {

        authService.delUSer(id);

        return ResponseEntity.ok(Map.of("success", true, "message", "Usuario eliminado satisfactoriamente"));
    }

}
