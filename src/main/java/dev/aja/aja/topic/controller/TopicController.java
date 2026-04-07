package dev.aja.aja.topic.controller;

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

import dev.aja.aja.topic.dto.TopicEditDTO;
import dev.aja.aja.topic.dto.TopicNewEditDTO;
import dev.aja.aja.topic.service.TopicService;

/**
 * Clase que nos va a gestionar todos los endpoints refentes a Topic, obtener,
 * añadir, editar, eliminar
 */
@Controller
@RequestMapping("/api")
public class TopicController {

    private final TopicService topicService;

    /**
     * Contructor del controlador con inyección de dependencias
     * 
     * @param topicService instancia de TopicService para acceder a la lógica de
     *                     esta feature
     */
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Para añadir un nuevo TopicEntity, utilizamos una versión simplificada
     * TopicNewEditDTO
     * 
     * @param topicNewDTO información básica y necesaria para añadir un TopicEntity
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje, si diera un error, indicaría el mensaje con el error
     */
    @PostMapping("/topic")
    public ResponseEntity<Map<String, Object>> addTopic(@RequestBody TopicNewEditDTO topicNewDTO) {

        topicService.addTopic(topicNewDTO);

        return ResponseEntity.ok(Map.of("success", true, "message", "Topic añadido satisfactoriamente"));
    }

    /**
     * Para eliminar un TopicEntity, indicamos el id. Esto eliminará todo su
     * contenido
     * 
     * @param id id el topic que se quiere eliminar
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje, si diera un error, indicaría el mensaje con el error
     */
    @DeleteMapping("/topic/{id}")
    public ResponseEntity<Map<String, Object>> delTopic(@PathVariable Long id) {

        topicService.delTopic(id);

        return ResponseEntity.ok(Map.of("success", true, "message", "Topic eliminado satisfactoriamente"));
    }

    /**
     * Para editar un TopicEntity, se envía la información básica, title y a qué
     * Forum pertenece
     * 
     * @param topicEditDTO información para editar
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje, si diera un error, indicaría el mensaje con el error
     */
    @PutMapping("/topic")
    public ResponseEntity<Map<String, Object>> editTopic(@RequestBody TopicEditDTO topicEditDTO) {

        topicService.editTopic(topicEditDTO);

        return ResponseEntity.ok(Map.of("success", true, "message", "Topic editado satisfactoriamente"));
    }

    /**
     * Para obtener toda la información de un TopicEntity
     * 
     * @param id indicamos el id del TopicEntity deseado
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es una entidad de TopicEntity, si diera un error, indicaría el
     *         mensaje con el error
     */
    @GetMapping("/topic/{id}")
    public ResponseEntity<Map<String, Object>> getTopic(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of("success", true, "message", topicService.getTopic(id)));
    }

    /**
     * Para obtener todos los TopicEntity
     * 
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es una lista de TopicEntity, si diera un error, indicaría el
     *         mensaje con el error
     */
    @GetMapping("/topic")
    public ResponseEntity<Map<String, Object>> getAllTopic() {
        return ResponseEntity.ok(Map.of("success", true, "message", topicService.getAllTopic()));
    }

}
