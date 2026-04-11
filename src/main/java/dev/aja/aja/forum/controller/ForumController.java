package dev.aja.aja.forum.controller;

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

import dev.aja.aja.forum.dto.ForumEntityDTO;
import dev.aja.aja.forum.service.ForumService;

/**
 * Clase que nos va a gestionar todos los endpoints refentes a forum, obtener,
 * añadir, editar, eliminar
 */
@Controller
@RequestMapping("/api")
public class ForumController {

    private final ForumService forumService;

    /**
     * 
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param forumService inyección para servicio de forums, obtenemos
     *                     acceso a la lógica referente a forum
     */
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    /**
     * Añadir nuevo Forum, sólo con acceso para Administradores
     * 
     * @param forumEntityNewDTO record DTO para añadir un nuevo forum.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PostMapping("/forum")
    public ResponseEntity<Map<String, Object>> addForum(@RequestBody ForumEntityDTO forumEntityNewDTO) {

        forumService.addForum(forumEntityNewDTO);

        return ResponseEntity
                .ok(Map.of("success", true, "message", "Nuevo Forum añadido satisfactoriamente"));
    }

    /**
     * Eliminar Forum, sólo con acceso para Administradores
     * 
     * @param id id del forum que se quiere eliminar
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @DeleteMapping("/forum/{id}")
    public ResponseEntity<Map<String, Object>> delForum(@PathVariable Long id) {

        forumService.delForum(id);

        return ResponseEntity
                .ok(Map.of("success", true, "message", "Forum eliminado satisfactoriamente"));
    }

    /**
     * Editar Forum, sólo con acceso para Administradores
     * 
     * @param forumEditDTO record DTO para editar un forum
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PutMapping("/forum")
    public ResponseEntity<Map<String, Object>> editForum(@RequestBody ForumEntityDTO forumEditDTO) {

        forumService.editForum(forumEditDTO);

        return ResponseEntity
                .ok(Map.of("success", true, "message", "Forum editado satisfactoriamente"));
    }

    /**
     * Obtener todo el contenido de una entidad ForumEntity, acceso para cualquier
     * usuario cuando entra en esta sección
     * 
     * @param id identificador del forum al que se quiere acceder
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es toda la información de un ForumEntity, si hubiera algún fallo
     *         llegarían los diccionarios de las excepciones
     */
    @GetMapping("/forum/{id}")
    public ResponseEntity<Map<String, Object>> getForum(@PathVariable Long id) {

        return ResponseEntity
                .ok(Map.of("success", true, "message", forumService.getForum(id)));
    }

    /**
     * Obtener una lista de ForumEntity, acceso para cualquier
     * usuario cuando entra en esta sección
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es una lista de ForumEntityDTO, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @GetMapping("/forum")
    public ResponseEntity<Map<String, Object>> getAllForum() {
        return ResponseEntity
                .ok(Map.of("success", true, "message", forumService.getAllForums()));
    }

}