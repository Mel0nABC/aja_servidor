package dev.aja.aja.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.aja.aja.user.entity.UserEntity;
import dev.aja.aja.user.service.UserService;

/**
 * Clase que nos va a gestionar todos los endpoints refentes a usuario, obtener,
 * añadir, editar, eliminar
 */
@Controller
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     * 
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param userService inyección para servicio de usuario, obtenemos
     *                    acceso a la lígica referente a usuarios
     */
    public UserController(UserService userService) {
        this.userService = userService;
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

        userService.addUser(userEntity);

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

        userService.editUser(userEntity);

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

        userService.delUSer(id);

        return ResponseEntity.ok(Map.of("success", true, "message", "Usuario eliminado satisfactoriamente"));
    }

    /**
     * 
     * Obtener el usuario que hay en la base de datos.
     * 
     * @return Retornamos un DTO de UserEntity, success indica cuál ha sido el
     *         resultado y message el contenido. En este caso el contenido de
     *         respuesta válida es una lista de UserEntityDTO
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<Map<String, Object>> getUsers(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "message", userService.getUserDTO(id)));
    }

    /**
     * 
     * Obtener la lista de usuarios que hay en la base de datos.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es una lista de UserEntityDTO
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getAllUSers() {
        return ResponseEntity.ok(Map.of("success", true, "message", userService.getAllUSerDTO()));
    }
}
