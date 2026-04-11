package dev.aja.aja.post.controller;

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

import dev.aja.aja.post.dto.PostEditDTO;
import dev.aja.aja.post.dto.PostNewDTO;
import dev.aja.aja.post.service.PostService;

/**
 * Clase que nos va a gestionar todos los endpoints refentes a forum, obtener,
 * añadir, editar, eliminar
 */
@Controller
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    /**
     * 
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param postService inyección para servicio de post, obtenemos
     *                    acceso a la lógica referente a post
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Añadir nuevo post
     * 
     * @param postNewDTO record DTO para añadir un nuevo post.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PostMapping("/post")
    public ResponseEntity<Map<String, Object>> addPost(@RequestBody PostNewDTO postNewDTO) {
        postService.addPost(postNewDTO);
        return ResponseEntity.ok(Map.of("success", true, "message", "Post añadido satisfactoriamente"));
    }

    /**
     * Editar post
     * 
     * @param postEditDTO record DTO para editar un post.
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PutMapping("/post")
    public ResponseEntity<Map<String, Object>> editPost(@RequestBody PostEditDTO postEditDTO) {
        postService.editPost(postEditDTO);
        return ResponseEntity.ok(Map.of("success", true, "message", "Post editado satisfactoriamente"));
    }

    /**
     * Eliminar post
     * 
     * @param id id del post que se desea eliminar
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @DeleteMapping("/post/{id}")
    public ResponseEntity<Map<String, Object>> delPost(@PathVariable Long id) {
        postService.delPost(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "Post eliminado satisfactoriamente"));
    }

    /**
     * Obtener toda la información de un post concreto
     * 
     * @param id id del post que se desea obtener la información
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un PostEntityDTO, que obtiene la información necesaria para
     *         mostrar en el cliente
     */
    @GetMapping("/post/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "message", postService.getPost(id)));
    }

    /**
     * Obtener toda la información de todos los post
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es una lista de PostEntityDTO, que obtiene la información necesaria
     *         para mostrar en el cliente
     */
    @GetMapping("/post")
    public ResponseEntity<Map<String, Object>> getAllPost() {
        return ResponseEntity.ok(Map.of("success", true, "message", postService.getAllPost()));
    }

}
